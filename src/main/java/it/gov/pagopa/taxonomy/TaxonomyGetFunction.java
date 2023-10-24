package it.gov.pagopa.taxonomy;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.taxonomy.enums.ExtensionEnum;
import it.gov.pagopa.taxonomy.enums.VersionEnum;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.json.TaxonomyMetadata;
import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;
import it.gov.pagopa.taxonomy.util.AppUtil;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class TaxonomyGetFunction {

    private static String msg = null;
    private static final String VERSION_NOT_EXISTS_ERROR = AppMessageUtil.getMessage("version.not.exists.error");
    private static final String EXTENSION_NOT_EXISTS_ERROR = AppMessageUtil.getMessage("extension.not.exists.error");
    private static final String GENERIC_RETRIEVAL_ERROR = AppMessageUtil.getMessage("generic.retrieval.error");
    private static final String STORAGE_CONN_STRING = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
    private static final String BLOB_CONTAINER_NAME_OUTPUT = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
    private static final String BLOB_CONTAINER_NAME_INPUT = System.getenv("BLOB_CONTAINER_NAME_INPUT");
    private static final String JSON_NAME = System.getenv("JSON_NAME");
    private static final String CSV_NAME = System.getenv("CSV_NAME");
    private static ObjectMapper objectMapper = null;
    private static BlobContainerClient blobContainerClientOutput;
    private static BlobContainerClient blobContainerClientInput;
    private static BlobServiceClient blobServiceClient;

    private static BlobServiceClient getBlobServiceClient(){
        if(blobServiceClient == null){
            blobServiceClient = new BlobServiceClientBuilder().connectionString(STORAGE_CONN_STRING).buildClient();
        }
        return blobServiceClient;
    }

    private static BlobContainerClient getBlobContainerClientOutput(){
        if(blobContainerClientOutput == null){
            blobContainerClientOutput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_OUTPUT);
        }
        return blobContainerClientOutput;
    }
    private static BlobContainerClient getBlobContainerClientInput(){
        if(blobContainerClientInput == null){
            blobContainerClientInput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_INPUT);
        }
        return blobContainerClientInput;
    }

    private static ObjectMapper getObjectMapper(){
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    @FunctionName("FnHttpGet")
    public HttpResponseMessage getTaxonomy(
            @HttpTrigger(
                    name = "FnHttpGetTrigger",
                    methods = {HttpMethod.GET},
                    route = "taxonomy",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Logger logger = context.getLogger();

        try {
            Map<String, String> queryParams = request.getQueryParameters();
            String version = queryParams.getOrDefault("version", VersionEnum.STANDARD.toString());
            String extension = queryParams.getOrDefault("extension", ExtensionEnum.JSON.toString());
            if(!version.equalsIgnoreCase(VersionEnum.STANDARD.toString()) &&
                    !version.equalsIgnoreCase(VersionEnum.TOPICFLAG.toString())) {

                logger.info(VERSION_NOT_EXISTS_ERROR);
                String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                        .message(AppMessageUtil.getMessage(GENERIC_RETRIEVAL_ERROR))
                        .error(VERSION_NOT_EXISTS_ERROR)
                        .build());

                return AppUtil.writeResponse(request,
                        HttpStatus.BAD_REQUEST,
                        payload);
            }

            if(!extension.equalsIgnoreCase(ExtensionEnum.JSON.toString()) &&
                !extension.equalsIgnoreCase(ExtensionEnum.CSV.toString())) {

                logger.info(EXTENSION_NOT_EXISTS_ERROR);
                String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message(AppMessageUtil.getMessage(GENERIC_RETRIEVAL_ERROR))
                    .error(EXTENSION_NOT_EXISTS_ERROR)
                    .build());

                return AppUtil.writeResponse(request,
                    HttpStatus.BAD_REQUEST,
                    payload);
            }

            if(extension.equalsIgnoreCase("CSV")) {
                byte[] taxonomyCsv = getTaxonomyCsv(logger);
                HttpResponseMessage.Builder response = request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "text/csv; charset=utf-8")
                    .header("Content-Disposition", "attachment;filename=taxonomy.csv")
                    .body(taxonomyCsv);
                return response.build();
            }

            String taxonomyJson = getTaxonomyList(logger, version);
            TaxonomyMetadata taxonomyMetadata = getMetadata(logger);

            Map<String, String> map = new LinkedHashMap<>();
            map.put(AppConstant.RESPONSE_HEADER_UUID, taxonomyMetadata.getUuid());
            map.put(AppConstant.RESPONSE_HEADER_CREATED, taxonomyMetadata.getCreated().toString());
            map.put(AppConstant.RESPONSE_HEADER_VERSION, version);

            return AppUtil.writeResponseWithHeaders(request,
                    HttpStatus.OK,
                    taxonomyJson,
                    map);

        } catch (AppException e) {
            logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Get] AppException at {0}\n {1}", Instant.now(), ExceptionUtils.getMessage(e)));

            String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message(AppMessageUtil.getMessage(GENERIC_RETRIEVAL_ERROR))
                    .error(e.getCodeMessage().message(e.getArgs()))
                    .build());
            return AppUtil.writeResponse(request,
                HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
                payload);
        } catch (BlobStorageException e) {
                if(e.getErrorCode().equals(BlobErrorCode.BLOB_NOT_FOUND)) {
                    logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Get] BlobStorageException at {0}\n {1}",Instant.now(), ExceptionUtils.getStackTrace(e)));

                    AppException appException = new AppException(e, AppErrorCodeMessageEnum.BLOB_NOT_FOUND_JSON_ERROR);
                    String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                        .message(GENERIC_RETRIEVAL_ERROR)
                        .error(appException.getCodeMessage().message(appException.getArgs()))
                        .build());
                    return AppUtil.writeResponse(request,
                        HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
                        payload
                    );
                } else {
                    logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Get] BlobStorageException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));
                    AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
                    String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                        .message(GENERIC_RETRIEVAL_ERROR)
                        .error(appException.getCodeMessage().message(appException.getArgs()))
                        .build());
                    return AppUtil.writeResponse(request,
                        HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
                        payload
                    );
                }
        } catch (Exception e) {
            logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Get] GenericError at {0}\n {1}", Instant.now(), ExceptionUtils.getMessage(e)));

            AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
            String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message(AppMessageUtil.getMessage(GENERIC_RETRIEVAL_ERROR))
                    .error(appException.getCodeMessage().message(appException.getArgs()))
                    .build());
            return AppUtil.writeResponse(request,
                    HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
                    payload);
        }
    }

    private static TaxonomyMetadata getMetadata(Logger logger) {
       try {
            msg = MessageFormat.format("Retrieving the metadata json file from the blob storage at: [{0}]", Instant.now());
            logger.info(msg);
            String content = getBlobContainerClientOutput()
                .getBlobClient("taxonomy_metadata.json")
                .downloadContent()
                .toString();
            return getObjectMapper().readValue(content, TaxonomyMetadata.class);
        } catch (JsonProcessingException exception) {
            logger.info("An AppException has occurred");
            logger.info("Problem mapping Metadata Object from JSON file");
            throw new AppException(exception, AppErrorCodeMessageEnum.JSON_PARSING_ERROR);
        }
    }
    private static String getTaxonomyList(Logger logger, String version) {
        final String STANDARD_JSON_NAME = JSON_NAME.split("\\.")[0]+"_standard.json";
        final String TOPIC_JSON_NAME = JSON_NAME.split("\\.")[0]+"_topic.json";
        msg = MessageFormat.format("Retrieving the {0} json file from the blob storage at: [{1}]", version, Instant.now());
        logger.info(msg);
        String JSON_FILE = STANDARD_JSON_NAME;
        if(version.equalsIgnoreCase("topicflag"))
            JSON_FILE = TOPIC_JSON_NAME;
        String content = getBlobContainerClientOutput()
            .getBlobClient(JSON_FILE)
            .downloadContent()
            .toString();
        return content;
    }
    private static byte[] getTaxonomyCsv(Logger logger) {
        try {
            msg=MessageFormat.format("Retrieving the csv file from the blob storage at: [{0}]", Instant.now());
            logger.info(msg);
            InputStream blobStream = getBlobContainerClientInput()
                .getBlobClient(CSV_NAME)
                .openInputStream();
            byte[] byteArray = blobStream.readAllBytes();
            blobStream.close();
            return byteArray;
        } catch (IOException ioException) {
            logger.info("An AppException has occurred while parsing CSV file into byte array");
            throw new AppException(ioException, AppErrorCodeMessageEnum.ERROR);
        }
    }

}
