package it.gov.pagopa.taxonomy;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.model.json.TaxonomyStandard;
import it.gov.pagopa.taxonomy.model.json.TaxonomyTopicFlag;
import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyGetFunction {

    private static final String storageConnString = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
    private static final String blobContainerNameOutput = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
    private static final String jsonName = System.getenv("JSON_NAME");
    private static ObjectMapper objectMapper = null;
    private static BlobContainerClient blobContainerClientOutput;
    private static BlobServiceClient blobServiceClient;

    private static BlobServiceClient getBlobServiceClient(){
        if(blobServiceClient == null){
            blobServiceClient = new BlobServiceClientBuilder().connectionString(storageConnString).buildClient();
        }
        return blobServiceClient;
    }

    private static BlobContainerClient getBlobContainerClientOutput(){
        if(blobContainerClientOutput == null){
            blobContainerClientOutput = getBlobServiceClient().createBlobContainerIfNotExists(blobContainerNameOutput);
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
            String version = queryParams.get("version");

            if(version == null || version.isEmpty()) {
                logger.info("Version not specified, will convert into standard");
                version = "standard";
            } else if(!version.equalsIgnoreCase("standard") &&
                    !version.equalsIgnoreCase("topicflag")) {
                logger.info("Unknown version has been specified");
                String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                        .message("Taxonomy retrieval failed")
                        .error("Unknown version has been specified")
                        .build());
                return AppUtil.writeResponse(request,
                        HttpStatus.NOT_IMPLEMENTED,
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
            logger.log(Level.SEVERE, "[ALERT][Get] AppException at " + Instant.now() + "\n" + ExceptionUtils.getStackTrace(e), e);
            String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message("Taxonomy retrieval failed")
                    .error(e.getCodeMessage().message(e.getArgs()))
                    .build());
            return AppUtil.writeResponse(request,
                    HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
                    payload);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "[ALERT][Get] GenericError at " + Instant.now() + "\n" + ExceptionUtils.getStackTrace(e), e);
            AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
            String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message("Taxonomy retrieval failed")
                    .error(appException.getCodeMessage().message(appException.getArgs()))
                    .build());
            return AppUtil.writeResponse(request,
                    HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
                    payload);
        }
    }

    private static TaxonomyJson getTaxonomy(Logger logger) {
        try {
            Instant now = Instant.now();
            logger.info("Retrieving the json file from the blob storage at: [" + now + "]");
            String content = getBlobContainerClientOutput().getBlobClient(jsonName).downloadContent().toString();
            return getObjectMapper().readValue(content, TaxonomyJson.class);
        } catch (JsonProcessingException parsingException) {
            logger.info("An AppException has occurred");
            throw new AppException(parsingException, AppErrorCodeMessageEnum.JSON_PARSING_ERROR);
        }
    }

    private static String generatePayload(Logger logger, String version, TaxonomyJson taxonomyJson) {
        String payload = null;
        if (version.equalsIgnoreCase("standard")) {
            logger.info("Versioning json id = [" + taxonomyJson.getUuid() + "] to the standard version");
            List<TaxonomyStandard> taxonomyList = getObjectMapper().convertValue(taxonomyJson.getTaxonomyList(), new TypeReference<>() {});
            payload = AppUtil.getPayload(getObjectMapper(), taxonomyList);
            logger.info("Standard taxonomy retrieved successfully");
        } else if (version.equalsIgnoreCase("topicflag")) {
            logger.info("Versioning json id = [" + taxonomyJson.getUuid() + "] to the topic-flag version");
            List<TaxonomyTopicFlag> taxonomyList = getObjectMapper().convertValue(taxonomyJson.getTaxonomyList(), new TypeReference<>() {});
            payload = AppUtil.getPayload(getObjectMapper(), taxonomyList);
            logger.info("Topic-flag taxonomy retrieved successfully");
        }
        return payload;
    }
}
