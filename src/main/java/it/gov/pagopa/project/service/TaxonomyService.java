//package it.gov.pagopa.project.service;
//import com.azure.core.util.BinaryData;
//import com.azure.storage.blob.BlobClient;
//import com.azure.storage.blob.BlobContainerClient;
//import com.azure.storage.blob.BlobServiceClient;
//import com.azure.storage.blob.BlobServiceClientBuilder;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.opencsv.bean.CsvToBeanBuilder;
//import it.gov.pagopa.project.constants.Version;
//import it.gov.pagopa.project.exception.AppErrorCodeMessageEnum;
//import it.gov.pagopa.project.exception.AppException;
//import it.gov.pagopa.project.exception.AppResponse;
////import it.gov.pagopa.project.exception.ResponseMessage;
//import it.gov.pagopa.project.model.TaxonomyObject;
//import it.gov.pagopa.project.model.TaxonomyObjectDatalake;
//import it.gov.pagopa.project.model.TaxonomyObjectStandard;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Properties;
//import org.jboss.logging.Logger;
//
//import java.net.ConnectException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//
//public class TaxonomyService {
//
//  private String stringUrl;
//  private String jsonName;
//  private String azureConnString;
//  private ObjectMapper objectMapper;
//  private BlobServiceClient blobServiceClient;
//  private BlobContainerClient blobContainerClient;
//  private BlobClient blobClient;
//  private Properties properties;
//
//  //TEST
//  private Boolean isTest;
//  private String jsonString;
//  //TEST
//
//  public TaxonomyService() {
//  }
//
//  private static final Logger logger = Logger.getLogger(TaxonomyService.class);
//
//  public void updateTaxonomy() {
//    try {
//      List<TaxonomyObject> objectList = new CsvToBeanBuilder<TaxonomyObject>(
//          new InputStreamReader(new URL(stringUrl).openStream(), StandardCharsets.UTF_8))
//          .withSeparator(';')
//          .withSkipLines(0)
//          .withType(TaxonomyObject.class)
//          .build()
//          .parse();
//      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);
//      blobClient.upload(BinaryData.fromBytes(jsonBytes), true);
//      logger.info("Taxonomy updated successfully.");
//      /*List<TaxonomyObjectStandard> standardList = objectMapper.convertValue(objectList, new TypeReference<>() {});
//      List<TaxonomyObjectDatalake> datalakeList = objectMapper.convertValue(objectList, new TypeReference<>() {});
//      String standardJsonString = objectMapper.writeValueAsString(standardList);
//      String datalakeJsonString = objectMapper.writeValueAsString(datalakeList);
//      Files.write(Paths.get(properties.getProperty("STANDARD_JSON_PATH")), standardJsonString.getBytes());
//      Files.write(Paths.get(properties.getProperty("DATALAKE_JSON_PATH")), datalakeJsonString.getBytes());*/
//      //return new AppResponse(ResponseMessage.TAXONOMY_UPDATED);
//    } catch (ConnectException connException) {
//      logger.error("Failed to establish a connection.");
//      throw new AppException(connException, AppErrorCodeMessageEnum.CONNECTION_REFUSED, stringUrl);
//      //return new AppResponse(ResponseMessage.CONNECTION_REFUSED);
//    } catch (MalformedURLException e) {
//      throw new RuntimeException(e);
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
////    catch (FileNotFoundException fnfException) {
////      logger.error("Failed to read CSV file or failed to write JSON.");
////      return new AppResponse(ResponseMessage.FILE_DOES_NOT_EXIST);
////    } catch (MalformedURLException muException) {
////      logger.error("Malformed URL exception.");
////
////      return new AppResponse(ResponseMessage.MALFORMED_URL);
////    } catch (IOException ioException) {
////      logger.error("Error occurred while reading/writing.");
////      return new AppResponse(ResponseMessage.ERROR_READING_WRITING);
////    } catch (IllegalStateException isException) {
////      logger.error("CSV parsing error.");
////      return new AppResponse(ResponseMessage.CSV_PARSING_ERROR);
////    } catch(Exception e) {
////      if(e.getCause().toString().startsWith("com.opencsv.exceptions.CsvRequiredFieldEmptyException")){
////        logger.error("Malformed CSV.");
////        return new AppResponse(ResponseMessage.MALFORMED_CSV);
////      }
////        logger.error("Error occurred during update.");
////        return new AppResponse(ResponseMessage.GENERATE_FILE);
////      }
//  }
//
//
////  public AppResponse getTaxonomyList(String version) {
////    List<TaxonomyObject> taxonomyGeneric;
////    String taxonomy = "";
////    try {
////      if (!isTest){
////        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////        blobClient.downloadStream(outputStream);
////        taxonomy = outputStream.toString(StandardCharsets.UTF_8);
////      } else {
////        taxonomy = jsonString;
////      }
////      if (version.equalsIgnoreCase(Version.STANDARD.toString())) {
////        List<TaxonomyObjectStandard> tempList = objectMapper.readValue(taxonomy, new TypeReference<>() {});
////        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<>() {});
////      } else {
////        List<TaxonomyObjectDatalake> tempList = objectMapper.readValue(taxonomy, new TypeReference<>() {});
////        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<>() {});
////      }
////      logger.info("Successfully retrieved the taxonomy version.");
////      return new AppResponse(ResponseMessage.TAXONOMY_UPDATED, taxonomyGeneric);
////    } catch (JsonProcessingException jpExc) {
////      logger.error("Failed to parse JSON file.");
////      return new AppResponse(ResponseMessage.JSON_PARSING_ERROR);
////    } catch (Exception exc) {
////      logger.error("Internal server error.");
////      if(exc.getMessage().contains("BlobNotFound")) {
////        return new AppResponse(ResponseMessage.JSON_NOT_FOUND);
////      }
////      return new AppResponse(ResponseMessage.INTERNAL_SERVER_ERROR);
////    }
////  }
//
//}
