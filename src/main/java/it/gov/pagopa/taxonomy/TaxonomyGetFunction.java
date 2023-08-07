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
import it.gov.pagopa.taxonomy.model.json.StandardTaxonomy;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyGetFunction {

    private static final String storageConnString = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
    private static final String blobContainerName = System.getenv("BLOB_CONTAINER_NAME");
    private static final String blobName = System.getenv("JSON_NAME");

    private static ObjectMapper objectMapper = null;

    private static BlobContainerClient blobContainerClient;

    private static BlobContainerClient getBlobContainerClient(){
        if(blobContainerClient == null){
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(storageConnString).buildClient();
            blobContainerClient = blobServiceClient.createBlobContainerIfNotExists(blobContainerName);
        }
        return blobContainerClient;
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
            final ExecutionContext context) throws JsonProcessingException {
        Logger logger = context.getLogger();

        try {
            TaxonomyJson taxonomyJson = getTaxonomy(logger);
            Map<String, String> map = new LinkedHashMap<>();
            map.put(AppConstant.RESPONSE_HEADER_UUID, taxonomyJson.getUuid());
            map.put(AppConstant.RESPONSE_HEADER_CREATED, taxonomyJson.getCreated().toString());

            ModelMapper modelMapper = new ModelMapper();
            List<StandardTaxonomy> standardTaxonomyList = modelMapper.map(taxonomyJson.getTaxonomyList(), new TypeToken<List<StandardTaxonomy>>(){}.getType());
            String payload = AppUtil.getPayload(getObjectMapper(), standardTaxonomyList);
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
            String content = getBlobContainerClient().getBlobClient(blobName).downloadContent().toString();

            logger.info("Versioning the json");
            return getObjectMapper().readValue(content, TaxonomyJson.class);
        } catch (JsonProcessingException parsingException) {
            throw new AppException(parsingException, AppErrorCodeMessageEnum.JSON_PARSING_ERROR);
        }
    }
}