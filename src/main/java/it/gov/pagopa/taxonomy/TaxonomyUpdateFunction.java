package it.gov.pagopa.taxonomy;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.csv.TaxonomyCsv;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.function.Message;
import it.gov.pagopa.taxonomy.model.json.Taxonomy;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.modelmapper.ModelMapper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaxonomyUpdateFunction {

  private static final String storageConnString = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
  private static final String blobContainerNameInput = System.getenv("BLOB_CONTAINER_NAME_INPUT");
  private static final String blobContainerNameOuput = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
  private static final String jsonName = System.getenv("JSON_NAME");
  private static final String csvName = System.getenv("CSV_NAME");
  private static ObjectMapper objectMapper = null;

  private static ModelMapper modelMapper = null;
  private static BlobContainerClient blobContainerClientInput;
  private static BlobContainerClient blobContainerClientOutput;
  private static BlobServiceClient blobServiceClient;

  private static BlobServiceClient getBlobServiceClient(){
    if(blobServiceClient == null){
      blobServiceClient = new BlobServiceClientBuilder().connectionString(storageConnString).buildClient();
    }
    return blobServiceClient;
  }

  private static BlobContainerClient getBlobContainerClientInput(){
    if(blobContainerClientInput == null){
      blobContainerClientInput = getBlobServiceClient().createBlobContainerIfNotExists(blobContainerNameInput);
    }
    return blobContainerClientInput;
  }

  private static BlobContainerClient getBlobContainerClientOutput(){
    if(blobContainerClientOutput == null){
      blobContainerClientOutput = getBlobServiceClient().createBlobContainerIfNotExists(blobContainerNameOuput);
    }
    return blobContainerClientOutput;
  }


  private static ObjectMapper getObjectMapper(){
    if(objectMapper == null){
      objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
    }
    return objectMapper;
  }

  private static ModelMapper getModelMapper(){
    if(modelMapper == null){
      modelMapper = new ModelMapper();
    }
    return modelMapper;
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
      String payload = AppUtil.getPayload(getObjectMapper(), Message.builder().message("Taxonomy updated successfully").build());
      logger.info("Taxonomy updated successfully");
      return AppUtil.writeResponse(request,
              HttpStatus.OK,
              payload
              );

    } catch (AppException e) {
      logger.log(Level.SEVERE, "[ALERT] AppException at " + Instant.now(), e);
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
              .message("Taxonomy update failed")
              .error(e.getCodeMessage().message(e.getArgs()))
              .build());
      return AppUtil.writeResponse(request,
              HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
              payload
              );

    } catch (Exception e) {
      logger.log(Level.SEVERE, "[ALERT] Generic error at " + Instant.now(), e);
      AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
              .message("Taxonomy update failed")
              .error(appException.getCodeMessage().message(appException.getArgs()))
              .build());
      return AppUtil.writeResponse(request,
              HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
              payload
              );
    }
  }

  private static void updateTaxonomy(Logger logger) {
    try {
      logger.info("Download csv file [" + csvName + "] from blob at [" + Instant.now().toString() + "]");

      InputStreamReader inputStreamReader = new InputStreamReader(getBlobContainerClientInput().getBlobClient(csvName).downloadContent().toStream(), StandardCharsets.UTF_8);

      logger.info("Converting [" + csvName + "] into [" + jsonName + "]");
      List<TaxonomyCsv> taxonomyCsvList = new CsvToBeanBuilder<TaxonomyCsv>(inputStreamReader)
              .withSeparator(';')
              .withSkipLines(0)
              .withType(TaxonomyCsv.class)
              .build()
              .parse();

      Instant now = Instant.now();
      String id = UUID.randomUUID().toString();
      TaxonomyJson taxonomyJson = TaxonomyJson.builder()
              .uuid(id)
              .created(now)
              .taxonomyList(taxonomyCsvList.stream().map(taxonomyCsv ->
                getModelMapper().map(taxonomyCsv, Taxonomy.class)
              ).collect(Collectors.toList()))
              .build();

      byte[] jsonBytes = getObjectMapper().writeValueAsBytes(taxonomyJson);

      logger.info("Uploading json id = [" + id + "] created at: [" + now + "]");
      getBlobContainerClientOutput().getBlobClient(jsonName).upload(BinaryData.fromBytes(jsonBytes), true);

    } catch (JsonProcessingException | IllegalStateException parsingException) {
      logger.info("An AppException has occurred");
      throw new AppException(parsingException, AppErrorCodeMessageEnum.CSV_PARSING_ERROR);
    }
  }
}
