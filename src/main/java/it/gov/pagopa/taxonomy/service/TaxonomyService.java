package it.gov.pagopa.taxonomy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.taxonomy.constants.Version;
import it.gov.pagopa.taxonomy.exception.AppError;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.mapper.TaxonomyMapper;
import it.gov.pagopa.taxonomy.model.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TaxonomyService {

  @Value("${taxonomy.csvLink}")
  String stringUrl;

  @Value("${taxonomy.jsonName}")
  String jsonName;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  TaxonomyMapper taxonomyMapper;

  private static final Logger logger = Logger.getLogger(TaxonomyService.class);

  public void updateTaxonomy() {
    try {
      List<TaxonomyObjectCsv> objects = new CsvToBeanBuilder<TaxonomyObjectCsv>(new InputStreamReader(new URL(stringUrl).openStream(), StandardCharsets.UTF_8))
              .withSeparator(';')
              .withSkipLines(0)
              .withType(TaxonomyObjectCsv.class)
              .build()
              .parse();
      List<TaxonomyObject> objectList = taxonomyMapper.taxonomyCsvListToTaxonomyObjectList(objects);
      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);
      FileOutputStream outputStream = new FileOutputStream(jsonName);
      outputStream.write(jsonBytes);
      outputStream.close();
      logger.info("Taxonomy updated successfully.");
    } catch (ConnectException connException) {
      logger.error("Failed to establish a connection.");
      throw new AppException(AppError.CONNECTION_REFUSED);
    } catch (FileNotFoundException fnfException) {
      logger.error("Failed to retrieve the file.");
      throw new AppException(AppError.FILE_DOES_NOT_EXIST);
    } catch (MalformedURLException muException) {
      logger.error("Malformed URL exception.");
      throw new AppException(AppError.MALFORMED_URL);
    } catch (IOException ioException) {
      logger.error("Error occurred while reading/writing.");
      throw new AppException(AppError.ERROR_READING_WRITING);
    } catch (IllegalStateException isException) {
      logger.error("CSV parsing error.");
      throw new AppException(AppError.CSV_PARSING_ERROR);
    } catch (Exception e) {
      logger.error("Error occurred during update.");
      throw new AppException(AppError.GENERATE_FILE);
    }
  }

  public List<? extends TaxonomyObjectStandard> getTaxonomyList(String version) {
    List<? extends TaxonomyObjectStandard> taxonomyGeneric;
    try {
      String taxonomy = new String(Files.readAllBytes(Paths.get(jsonName)), StandardCharsets.UTF_8);
      if (version.equalsIgnoreCase(Version.STANDARD.toString())) {
        taxonomyGeneric = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectStandard>>() {});
      } else if (version.equalsIgnoreCase(Version.DATALAKE.toString())) {
        taxonomyGeneric = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectDatalake>>() {});
      } else {
        logger.error("The version does not exist.");
        throw new AppException(AppError.VERSION_DOES_NOT_EXIST);
      }
      logger.info("Successfully retrieved the taxonomy version.");
      return taxonomyGeneric;
    } catch (NoSuchFileException nsf) {
      logger.error("Failed to retrieve the file.");
      throw new AppException(AppError.FILE_DOES_NOT_EXIST);
    } catch (Exception exc) {
      logger.error("Internal server error.");
      throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }
  }
}
