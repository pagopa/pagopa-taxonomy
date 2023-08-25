package it.gov.pagopa.taxonomy;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
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
import it.gov.pagopa.taxonomy.model.json.TaxonomyTopicFlag;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaxonomyUpdateFunction {

  private static final String UPDATE_FAILED = AppMessageUtil.getMessage("update.failed");
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

  @FunctionName("FnHttpGenerate")
  public HttpResponseMessage updateTaxonomy(
      @HttpTrigger(
          name = "FnHttpGenerateTrigger",
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
      logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Get][Triggered] AppException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
          .message(UPDATE_FAILED)
          .error(e.getCodeMessage().message(e.getArgs()))
          .build());
      return AppUtil.writeResponse(request,
          HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
          payload
      );

    } catch (BlobStorageException e) {
      if(e.getErrorCode().equals(BlobErrorCode.BLOB_NOT_FOUND)) {
        logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Update] BlobStorageException at {0} \n {1}" , Instant.now(), ExceptionUtils.getStackTrace(e)));

        AppException appException = new AppException(e, AppErrorCodeMessageEnum.BLOB_NOT_FOUND_CSV_ERROR);
        String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
            .message(UPDATE_FAILED)
            .error(appException.getCodeMessage().message(appException.getArgs()))
            .build());
        return AppUtil.writeResponse(request,
            HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
            payload
        );
      } else {
        logger.log(Level.SEVERE,MessageFormat.format( "[ALERT][Update] BlobStorageException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));
        AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
        String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
            .message(UPDATE_FAILED)
            .error(appException.getCodeMessage().message(appException.getArgs()))
            .build());
        return AppUtil.writeResponse(request,
            HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
            payload
        );
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE,MessageFormat.format("[ALERT][Get][Triggered] Generic error at {0}\n {1}",Instant.now(), ExceptionUtils.getStackTrace(e)));

      AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
              .message(UPDATE_FAILED)
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
      logger.info(MessageFormat.format("Download csv file [{0}] from blob at [{1}]", CSV_NAME,Instant.now()));
      InputStreamReader inputStreamReader = new InputStreamReader(getBlobContainerClientInput().getBlobClient(CSV_NAME).downloadContent().toStream(), StandardCharsets.UTF_8);

      logger.info(MessageFormat.format("Converting [{0}] into [{1}]",CSV_NAME,JSON_NAME));
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
