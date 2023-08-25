package it.gov.pagopa.taxonomy;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.taxonomy.enums.VersionEnum;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.model.json.TaxonomyStandard;
import it.gov.pagopa.taxonomy.model.json.TaxonomyTopicFlag;
import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyGetFunction {

    private static String msg = null;
    private static final String VERSION_NOT_EXISTS_ERROR = AppMessageUtil.getMessage("version.not.exists.error");
    private static final String GENERIC_RETRIEVAL_ERROR = AppMessageUtil.getMessage("generic.retrieval.error");
    private static final String STORAGE_CONN_STRING = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
    private static final String BLOB_CONTAINER_NAME_OUTPUT = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
    private static final String JSON_NAME = System.getenv("JSON_NAME");
    private static ObjectMapper objectMapper = null;
    private static BlobContainerClient blobContainerClientOutput;
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
                    route = "taxonomy/{version?}",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Logger logger = context.getLogger();

        try {
            Map<String, String> queryParams = request.getQueryParameters();
            String version = queryParams.getOrDefault("version", VersionEnum.STANDARD.toString());

            if(!version.equalsIgnoreCase(VersionEnum.STANDARD.toString()) &&
                    !version.equalsIgnoreCase(VersionEnum.TOPICFLAG.toString())) {

                logger.info(AppMessageUtil.getMessage(VERSION_NOT_EXISTS_ERROR));
                String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                        .message(AppMessageUtil.getMessage(GENERIC_RETRIEVAL_ERROR))
                        .error(VERSION_NOT_EXISTS_ERROR)
                        .build());

                return AppUtil.writeResponse(request,
                        HttpStatus.BAD_REQUEST,
                        payload);
            }

            TaxonomyJson taxonomyJson = getTaxonomy(logger);

            Map<String, String> map = new LinkedHashMap<>();
            map.put(AppConstant.RESPONSE_HEADER_UUID, taxonomyJson.getUuid());
            map.put(AppConstant.RESPONSE_HEADER_CREATED, taxonomyJson.getCreated().toString());
            map.put(AppConstant.RESPONSE_HEADER_VERSION, version);

            String payload = generatePayload(logger, version, taxonomyJson);

            return AppUtil.writeResponseWithHeaders(request,
                    HttpStatus.OK,
                    payload,
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

    private static TaxonomyJson getTaxonomy(Logger logger) {
        try {
            msg=MessageFormat.format("Retrieving the json file from the blob storage at: [{0}]", Instant.now());
            logger.info(msg);
            String content = getBlobContainerClientOutput()
                    .getBlobClient(JSON_NAME)
                    .downloadContent()
                    .toString();
            return getObjectMapper().readValue(content, TaxonomyJson.class);
        } catch (JsonProcessingException parsingException) {
            logger.info("An AppException has occurred");
            throw new AppException(parsingException, AppErrorCodeMessageEnum.JSON_PARSING_ERROR);
        }
    }

    private static String generatePayload(Logger logger, String version, TaxonomyJson taxonomyJson) {
        String payload = null;
        if (version.equalsIgnoreCase(VersionEnum.STANDARD.toString())) {
            msg=MessageFormat.format("Versioning json id = [{0}] to the {1} version", taxonomyJson.getUuid(), VersionEnum.STANDARD);
            logger.info(msg);
            List<TaxonomyStandard> taxonomyList = getObjectMapper().convertValue(taxonomyJson.getTaxonomyList(), new TypeReference<>() {});
            payload = AppUtil.getPayload(getObjectMapper(), taxonomyList);
            msg=MessageFormat.format("{0} taxonomy retrieved successfully", VersionEnum.STANDARD);
            logger.info(msg);
        } else if (version.equalsIgnoreCase(VersionEnum.TOPICFLAG.toString())) {
            msg=MessageFormat.format("Versioning json id = [{0}] to the {1} version", taxonomyJson.getUuid(), VersionEnum.TOPICFLAG);
            logger.info(msg);

            List<TaxonomyTopicFlag> taxonomyList = getObjectMapper().convertValue(taxonomyJson.getTaxonomyList(), new TypeReference<>() {});
            payload = AppUtil.getPayload(getObjectMapper(), taxonomyList);
            msg=MessageFormat.format("{0} taxonomy retrieved successfully", VersionEnum.TOPICFLAG);
            logger.info(msg);
        }
        return payload;
    }
}
