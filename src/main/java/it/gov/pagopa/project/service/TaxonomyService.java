package it.gov.pagopa.project.service;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.project.constants.Version;
import it.gov.pagopa.project.exception.AppResponse;
import it.gov.pagopa.project.exception.ResponseMessage;
import it.gov.pagopa.project.model.TaxonomyObject;
import it.gov.pagopa.project.model.TaxonomyObjectDatalake;
import it.gov.pagopa.project.model.TaxonomyObjectStandard;

import java.io.*;
import java.util.List;
import java.util.Properties;
import org.jboss.logging.Logger;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TaxonomyService {

  private String stringUrl;
  private String jsonName;
  private String azureConnString;
  private ObjectMapper objectMapper;
  private BlobServiceClient blobServiceClient;
  private BlobContainerClient blobContainerClient;
  private BlobClient blobClient;
  private Properties properties;

  public TaxonomyService() {
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      properties = new Properties();
      properties.load(inputStream);
      this.stringUrl = properties.getProperty("CSV_URL");
      this.jsonName = properties.getProperty("JSON_NAME");
      this.azureConnString = properties.getProperty("AZURE_CONN_STRING");
      blobServiceClient = new BlobServiceClientBuilder()
          .connectionString(azureConnString)
          .buildClient();
      blobServiceClient.createBlobContainerIfNotExists(properties.getProperty("BLOB_CONTAINER_NAME"));
      blobContainerClient = blobServiceClient.getBlobContainerClient(properties.getProperty("BLOB_CONTAINER_NAME"));
      blobClient = blobContainerClient.getBlobClient(jsonName);
      objectMapper = new ObjectMapper();
    } catch (IOException e) {
      System.out.println("Error in service constructor");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static final Logger logger = Logger.getLogger(TaxonomyService.class);

  public AppResponse updateTaxonomy() {
    try {
      List<TaxonomyObject> objectList = new CsvToBeanBuilder<TaxonomyObject>(
          new InputStreamReader(new URL(stringUrl).openStream(), StandardCharsets.UTF_8))
          .withSeparator(';')
          .withSkipLines(0)
          .withType(TaxonomyObject.class)
          .build()
          .parse();
      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);
      blobClient.upload(BinaryData.fromBytes(jsonBytes), true);
      logger.info("Taxonomy updated successfully.");
      return new AppResponse(ResponseMessage.TAXONOMY_UPDATED);
    } catch (ConnectException connException) {
      logger.error("Failed to establish a connection.");
      return new AppResponse(ResponseMessage.CONNECTION_REFUSED);
    } catch (FileNotFoundException fnfException) {
      logger.error("Failed to read CSV file or failed to write JSON.");
      return new AppResponse(ResponseMessage.FILE_DOES_NOT_EXIST);
    } catch (MalformedURLException muException) {
      logger.error("Malformed URL exception.");
      return new AppResponse(ResponseMessage.MALFORMED_URL);
    } catch (IOException ioException) {
      logger.error("Error occurred while reading/writing.");
      return new AppResponse(ResponseMessage.ERROR_READING_WRITING);
    } catch (IllegalStateException isException) {
      logger.error("CSV parsing error.");
      return new AppResponse(ResponseMessage.CSV_PARSING_ERROR);
    } catch(Exception e) {
      if(e.getCause().toString().startsWith("com.opencsv.exceptions.CsvRequiredFieldEmptyException")){
        logger.error("Malformed CSV.");
        return new AppResponse(ResponseMessage.MALFORMED_CSV);
      }
        logger.error("Error occurred during update.");
        return new AppResponse(ResponseMessage.GENERATE_FILE);
      }
  }

  public AppResponse getTaxonomyList(String version) {
    List<TaxonomyObject> taxonomyGeneric;
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      blobClient.downloadStream(outputStream);
      String taxonomy = outputStream.toString(StandardCharsets.UTF_8);
      if (version.equalsIgnoreCase(Version.STANDARD.toString())) {
        List<TaxonomyObjectStandard> tempList = objectMapper.readValue(taxonomy, new TypeReference<>() {});
        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<>() {});
      } else {
        List<TaxonomyObjectDatalake> tempList = objectMapper.readValue(taxonomy, new TypeReference<>() {});
        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<>() {});
      }
      logger.info("Successfully retrieved the taxonomy version.");
      return new AppResponse(ResponseMessage.TAXONOMY_UPDATED, taxonomyGeneric);
    } catch (JsonProcessingException jpExc) {
      logger.error("Failed to parse JSON file.");
      jpExc.printStackTrace();
      return new AppResponse(ResponseMessage.JSON_PARSING_ERROR);
    } catch (Exception exc) {
      logger.error("Internal server error.");
      if(exc.getMessage().contains("BlobNotFound")) {
        return new AppResponse(ResponseMessage.JSON_NOT_FOUND);
      }
      return new AppResponse(ResponseMessage.INTERNAL_SERVER_ERROR);
    }
  }

}
