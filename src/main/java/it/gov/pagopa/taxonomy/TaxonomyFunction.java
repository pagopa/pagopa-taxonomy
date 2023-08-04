package it.gov.pagopa.taxonomy;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.TaxonomyObject;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyFunction {

  private static final String csvUrl = System.getenv("CSV_URL");
  private static final String storageConnString = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
  private static final String blobContainerName = System.getenv("BLOB_CONTAINER_NAME");
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static BlobContainerClient blobContainerClient;
  private static BlobContainerClient getBlobContainerClient(){
    if(blobContainerClient == null){
      BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(storageConnString).buildClient();
      blobContainerClient = blobServiceClient.createBlobContainerIfNotExists(blobContainerName);
    }
    return blobContainerClient;
  }

  @FunctionName("UpdateTrigger")
  public HttpResponseMessage updateTaxonomy(
      @HttpTrigger(
          name = "UpdateTrigger",
          methods = {HttpMethod.GET},
          route = "generate",
          authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    Logger logger = context.getLogger();
    try {
      updateTaxonomy(logger);

      return getResponse(request, null);
    } catch (AppException e) {
      logger.log(Level.SEVERE, "[ALERT] AppException at " + Instant.now(), e);
      return getResponse(request, e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "[ALERT] Generic error at " + Instant.now(), e);
      AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
      return getResponse(request, appException);
    }
  }

  private static HttpResponseMessage getResponse(HttpRequestMessage<Optional<String>> request, AppException e){
    if(e == null){
      return request.createResponseBuilder(HttpStatus.OK)
              .header("Content-Type", MediaType.APPLICATION_JSON)
              .body("{ \"message\" : \"Taxonomy updated successfully\" }")
              .build();
    } else {
      return request.createResponseBuilder(HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()))
              .header("Content-Type", MediaType.APPLICATION_JSON)
              .body("{ \"message\" : \"Taxonomy update failed\", \"error\" : " + e.getCodeMessage().message(e.getArgs()) + " }")
              .build();
    }
  }

  private static HttpResponseMessage getResponse(HttpRequestMessage<Optional<String>> request, HttpStatus httpStatus, String message){
    return request.createResponseBuilder(httpStatus)
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .body(message)
            .build();
  }

  private static void updateTaxonomy(Logger logger) {
    try {
      logger.info("Download csv ["+csvUrl+"]");
      InputStreamReader inputStreamReader = new InputStreamReader(new URL(csvUrl).openStream(), StandardCharsets.UTF_8);

      logger.info("Transform csv to json");
      List<TaxonomyObject> objectList = new CsvToBeanBuilder<TaxonomyObject>(inputStreamReader)
              .withSeparator(';')
              .withSkipLines(0)
              .withType(TaxonomyObject.class)
              .build()
              .parse();
      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);

      logger.info("Upload json "+csvUrl);
      getBlobContainerClient().getBlobClient("taxonomy.json").upload(BinaryData.fromBytes(jsonBytes), true);

      /*List<TaxonomyObjectStandard> standardList = objectMapper.convertValue(objectList, new TypeReference<>() {});
      List<TaxonomyObjectDatalake> datalakeList = objectMapper.convertValue(objectList, new TypeReference<>() {});
      String standardJsonString = objectMapper.writeValueAsString(standardList);
      String datalakeJsonString = objectMapper.writeValueAsString(datalakeList);
      Files.write(Paths.get(properties.getProperty("STANDARD_JSON_PATH")), standardJsonString.getBytes());
      Files.write(Paths.get(properties.getProperty("DATALAKE_JSON_PATH")), datalakeJsonString.getBytes());*/
      //return new AppResponse(ResponseMessage.TAXONOMY_UPDATED);
    } catch (ConnectException connException) {
      throw new AppException(connException, AppErrorCodeMessageEnum.CONNECTION_REFUSED, csvUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
//    catch (FileNotFoundException fnfException) {
//      logger.error("Failed to read CSV file or failed to write JSON.");
//      return new AppResponse(ResponseMessage.FILE_DOES_NOT_EXIST);
//    } catch (MalformedURLException muException) {
//      logger.error("Malformed URL exception.");
//
//      return new AppResponse(ResponseMessage.MALFORMED_URL);
//    } catch (IOException ioException) {
//      logger.error("Error occurred while reading/writing.");
//      return new AppResponse(ResponseMessage.ERROR_READING_WRITING);
//    } catch (IllegalStateException isException) {
//      logger.error("CSV parsing error.");
//      return new AppResponse(ResponseMessage.CSV_PARSING_ERROR);
//    } catch(Exception e) {
//      if(e.getCause().toString().startsWith("com.opencsv.exceptions.CsvRequiredFieldEmptyException")){
//        logger.error("Malformed CSV.");
//        return new AppResponse(ResponseMessage.MALFORMED_CSV);
//      }
//        logger.error("Error occurred during update.");
//        return new AppResponse(ResponseMessage.GENERATE_FILE);
//      }
  }

}
