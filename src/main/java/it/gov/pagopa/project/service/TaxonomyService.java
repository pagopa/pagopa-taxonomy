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
import it.gov.pagopa.project.model.TaxonomyObject;
import it.gov.pagopa.project.model.TaxonomyObjectDatalake;
import it.gov.pagopa.project.model.TaxonomyObjectStandard;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.jboss.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class TaxonomyService {

  private String stringUrl;
  private String jsonName;
  private ObjectMapper objectMapper;
  private BlobServiceClient blobServiceClient;
  private BlobContainerClient blobContainerClient;
  private BlobClient blobClient;
  private Properties properties;
  public TaxonomyService() {
    objectMapper = new ObjectMapper();
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      properties = new Properties();
      properties.load(inputStream);
      this.stringUrl = properties.getProperty("CSV_URL");
      this.jsonName = properties.getProperty("JSON_NAME");
      blobServiceClient = new BlobServiceClientBuilder()
          .connectionString(properties.getProperty("AZURE_CONN_STRING"))
          .buildClient();
      blobServiceClient.createBlobContainerIfNotExists(properties.getProperty("BLOB_CONTAINER_NAME"));
      blobContainerClient = blobServiceClient.getBlobContainerClient(properties.getProperty("BLOB_CONTAINER_NAME"));
      blobClient = blobContainerClient.getBlobClient(properties.getProperty("JSON_NAME"));
    } catch (IOException e) {
      System.out.println("Error in service constructor");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static final Logger logger = Logger.getLogger(TaxonomyService.class);

  public void updateTaxonomy() {
    try {
      List<TaxonomyObject> objectList = new CsvToBeanBuilder<TaxonomyObject>(
          new InputStreamReader(new URL(stringUrl).openStream(), StandardCharsets.UTF_8))
          .withSeparator(';')
          .withSkipLines(0)
          .withType(TaxonomyObject.class)
          .build()
          .parse();
      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);
      blobClient.upload(BinaryData.fromBytes(jsonBytes));
      logger.info("Taxonomy updated successfully.");
    } catch (ConnectException connException) {
      logger.error("Failed to establish a connection.");
      //throw new AppException(AppError.CONNECTION_REFUSED);
    } catch (FileNotFoundException fnfException) {
      logger.error("Failed to read CSV file or failed to write JSON.");
      //throw new AppException(AppError.FILE_DOES_NOT_EXIST);
    } catch (MalformedURLException muException) {
      logger.error("Malformed URL exception.");
      //throw new AppException(AppError.MALFORMED_URL);
    } catch (IOException ioException) {
      logger.error("Error occurred while reading/writing.");
      //throw new AppException(AppError.ERROR_READING_WRITING);
    } catch (IllegalStateException isException) {
      logger.error("CSV parsing error.");
      //throw new AppException(AppError.CSV_PARSING_ERROR);
    } catch (RuntimeException runtimeExc){
      if(runtimeExc.getCause().toString().startsWith("com.opencsv.exceptions.CsvRequiredFieldEmptyException")){
        logger.error("Malformed CSV.");
        //throw new AppException(AppError.MALFORMED_CSV);
      } else {
        //throw new AppException(AppError.CSV_PARSING_ERROR);
      }
    } catch(Exception e) {
        logger.error("Error occurred during update.");
        //throw new AppException(AppError.GENERATE_FILE);
      }
    }

  public List<TaxonomyObject> getTaxonomyList(String version) throws Exception {
    List<TaxonomyObject> taxonomyGeneric;
    try {
      String taxonomy = Files.readString(Paths.get(jsonName));
      if (version.equalsIgnoreCase(Version.STANDARD.toString())) {
        List<TaxonomyObjectStandard> tempList = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectStandard>>() {});
        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<List<TaxonomyObject>>() {});
      } else {
        List<TaxonomyObjectDatalake> tempList = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectDatalake>>() {});
        taxonomyGeneric = objectMapper.convertValue(tempList, new TypeReference<List<TaxonomyObject>>() {});
      }
      logger.info("Successfully retrieved the taxonomy version.");
      return taxonomyGeneric;
    } catch (NoSuchFileException nsf) {
      logger.error("Failed to retrieve the file.");
      nsf.printStackTrace();
      throw new Exception();
    } catch (JsonProcessingException jpExc) {
      logger.error("Failed to parse JSON file.");
      jpExc.printStackTrace();
      throw new Exception();
    }
    catch (Exception exc) {
      logger.error("Internal server error.");
      exc.printStackTrace();
      throw new Exception();
    }
  }
}
