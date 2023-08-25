package it.gov.pagopa.taxonomy;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.csv.TaxonomyCsv;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.model.json.TaxonomyTopicFlag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaxonomyUpdateFunctionTrigger {

  private static final String STORAGE_ACCOUNT_CONN_STRING = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
  private static final String BLOB_CONTAINER_NAME_INPUT = System.getenv("BLOB_CONTAINER_NAME_INPUT");
  private static final String BLOB_CONTAINER_NAME_OUTPUT = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
  private static final String JSON_NAME = System.getenv("JSON_NAME");
  private static final String CSV_NAME = System.getenv("CSV_NAME");
  private static ObjectMapper objectMapper = null;

  private static ModelMapper modelMapper = null;
  private static BlobContainerClient blobContainerClientInput;
  private static BlobContainerClient blobContainerClientOutput;
  private static BlobServiceClient blobServiceClient;

  private static BlobServiceClient getBlobServiceClient(){
    if(blobServiceClient == null){
      blobServiceClient = new BlobServiceClientBuilder().connectionString(STORAGE_ACCOUNT_CONN_STRING).buildClient();
    }
    return blobServiceClient;
  }

  private static BlobContainerClient getBlobContainerClientInput(){
    if(blobContainerClientInput == null){
      blobContainerClientInput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_INPUT);
    }
    return blobContainerClientInput;
  }

  private static BlobContainerClient getBlobContainerClientOutput(){
    if(blobContainerClientOutput == null){
      blobContainerClientOutput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_OUTPUT);
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
  @FunctionName("FnBlobTriggerGenerate")
  public void run(
          @BlobTrigger(name = "file",
                  dataType = "binary",
                  path = "%BLOB_CONTAINER_NAME_INPUT%/%CSV_NAME%",
                  connection = "STORAGE_ACCOUNT_CONN_STRING") byte[] content,
          final ExecutionContext context
  ) {
    Logger logger = context.getLogger();
    logger.info("Name: " + CSV_NAME + " Size: " + content.length + " bytes");


    try {
      updateTaxonomy(logger);
      logger.info("Taxonomy updated successfully");

    } catch (AppException e) {
      logger.log(Level.SEVERE,MessageFormat.format("[ALERT][Update][Triggered] AppException at {0}\n {1}",Instant.now().toString(), ExceptionUtils.getStackTrace(e)));

    } catch (Exception e) {
      logger.log(Level.SEVERE,MessageFormat.format("[ALERT][Update][Triggered] Generic error at {0}\n {1}",Instant.now(), ExceptionUtils.getStackTrace(e)));

    }
  }

  private static void updateTaxonomy(Logger logger) {
    try {
      String mes = MessageFormat.format("Download csv file [{0}] from blob at [{1}]", CSV_NAME, Instant.now());
      logger.info(mes);

      InputStreamReader inputStreamReader = new InputStreamReader(getBlobContainerClientInput().getBlobClient(CSV_NAME).downloadContent().toStream(), StandardCharsets.UTF_8);

      logger.info(MessageFormat.format("Converting [{0}] into [{1}]", CSV_NAME, JSON_NAME));
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
                getModelMapper().map(taxonomyCsv, TaxonomyTopicFlag.class)
              ).collect(Collectors.toList()))
              .build();

      byte[] jsonBytes = getObjectMapper().writeValueAsBytes(taxonomyJson);

      logger.info(MessageFormat.format("Uploading json id = [{0}] created at: [{1}]",id,now));
      getBlobContainerClientOutput().getBlobClient(JSON_NAME).upload(BinaryData.fromBytes(jsonBytes), true);

    } catch (JsonProcessingException | IllegalStateException parsingException) {
      logger.info("An AppException has occurred");
      throw new AppException(parsingException, AppErrorCodeMessageEnum.CSV_PARSING_ERROR);
    }
  }
}
