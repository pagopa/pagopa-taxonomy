package it.gov.pagopa.taxonomy;

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
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppUtil;

import java.time.Instant;
import java.util.LinkedHashMap;
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

    @FunctionName("GetTrigger")
    public HttpResponseMessage getTaxonomy(
            @HttpTrigger(
                    name = "GetTrigger",
                    methods = {HttpMethod.GET},
                    route = "taxonomy",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Logger logger = context.getLogger();

        try {
            TaxonomyJson taxonomyJson = getTaxonomy(logger);
            Map<String, String> map = new LinkedHashMap<>();
            map.put(AppConstant.RESPONSE_HEADER_UUID, taxonomyJson.getUuid());
            map.put(AppConstant.RESPONSE_HEADER_CREATED, taxonomyJson.getCreated().toString());

            String payload = AppUtil.getPayload(getObjectMapper(), taxonomyJson.getTaxonomyList());
            logger.info("Taxonomy retrieved successfully");
            return AppUtil.writeResponseWithHeaders(request,
                    HttpStatus.OK,
                    payload,
                    map);

        } catch (AppException e) {
            logger.log(Level.SEVERE, "[ALERT] AppException at " + Instant.now(), e);
            String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
                    .message("Taxonomy retrieval failed")
                    .error(e.getCodeMessage().message(e.getArgs()))
                    .build());
            return AppUtil.writeResponse(request,
                    HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
                    payload);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "[ALERT] Generic error at " + Instant.now(), e);
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
            logger.info("Retrieving standard json from the blob storage at: [" + now + "]");
            String content = getBlobContainerClientOutput().getBlobClient(jsonName).downloadContent().toString();
            TaxonomyJson taxonomyJson = getObjectMapper().readValue(content, TaxonomyJson.class);
            logger.info("Versioning json id = [" + taxonomyJson.getUuid() + "] to the standard version");
            return taxonomyJson;
        } catch (JsonProcessingException parsingException) {
            logger.info("An AppException has occurred");
            throw new AppException(parsingException, AppErrorCodeMessageEnum.JSON_PARSING_ERROR);
        }
    }
}
