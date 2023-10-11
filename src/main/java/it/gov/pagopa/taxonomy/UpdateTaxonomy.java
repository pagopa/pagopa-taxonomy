package it.gov.pagopa.taxonomy;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.csv.TaxonomyCsv;
import it.gov.pagopa.taxonomy.model.json.TaxonomyJson;
import it.gov.pagopa.taxonomy.model.json.TaxonomyTopicFlag;
import org.modelmapper.ModelMapper;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UpdateTaxonomy {
    private UpdateTaxonomy() {
    }

    private static String msg = null;
    private static final String JSON_NAME = System.getenv("JSON_NAME");
    private static final String CSV_NAME = System.getenv("CSV_NAME");
    private static final String STORAGE_ACCOUNT_CONN_STRING = System.getenv("STORAGE_ACCOUNT_CONN_STRING");
    private static final String BLOB_CONTAINER_NAME_INPUT = System.getenv("BLOB_CONTAINER_NAME_INPUT");
    private static final String BLOB_CONTAINER_NAME_OUTPUT = System.getenv("BLOB_CONTAINER_NAME_OUTPUT");
    private static ObjectMapper objectMapper = null;
    private static ModelMapper modelMapper = null;
    private static BlobContainerClient blobContainerClientInput;
    private static BlobContainerClient blobContainerClientOutput;
    private static BlobServiceClient blobServiceClient;

    private static BlobServiceClient getBlobServiceClient() {
        if (blobServiceClient == null) {
            blobServiceClient = new BlobServiceClientBuilder().connectionString(STORAGE_ACCOUNT_CONN_STRING).buildClient();
        }
        return blobServiceClient;
    }

    private static BlobContainerClient getBlobContainerClientInput() {
        if (blobContainerClientInput == null) {
            blobContainerClientInput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_INPUT);
        }
        return blobContainerClientInput;
    }

    private static BlobContainerClient getBlobContainerClientOutput() {
        if (blobContainerClientOutput == null) {
            blobContainerClientOutput = getBlobServiceClient().createBlobContainerIfNotExists(BLOB_CONTAINER_NAME_OUTPUT);
        }
        return blobContainerClientOutput;
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    private static ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
        }
        return modelMapper;
    }

    public static void updateTaxonomy(Logger logger) {
        try {
            msg = MessageFormat.format("Download csv file [{0}] from blob at [{1}]", CSV_NAME, Instant.now());
            logger.info(msg);
            InputStreamReader inputStreamReader = new InputStreamReader(getBlobContainerClientInput().getBlobClient(CSV_NAME).downloadContent().toStream(), StandardCharsets.UTF_8);

            msg = MessageFormat.format("Converting [{0}] into [{1}]", CSV_NAME, JSON_NAME);
            logger.info(msg);
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

            msg = MessageFormat.format("Uploading json id = [{0}] created at: [{1}]", id, now);
            logger.info(msg);
            getBlobContainerClientOutput().getBlobClient(JSON_NAME).upload(BinaryData.fromBytes(jsonBytes), true);

        } catch (JsonProcessingException | IllegalStateException parsingException) {
            logger.info("An AppException has occurred");
            throw new AppException(parsingException, AppErrorCodeMessageEnum.CSV_PARSING_ERROR);
        }
    }
}
