package it.gov.pagopa.microservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import it.gov.pagopa.microservice.exception.AppError;
import it.gov.pagopa.microservice.exception.AppException;
import it.gov.pagopa.microservice.mapper.TaxonomyMapper;
import it.gov.pagopa.microservice.model.*;
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
              .withSkipLines(1)
              .withType(TaxonomyObjectCsv.class)
              .build()
              .parse();
      List<TaxonomyObject> objectList = taxonomyMapper.taxonomyCsvListToTaxonomyObjectList(objects);
      byte[] jsonBytes = objectMapper.writeValueAsBytes(objectList);
      FileOutputStream outputStream = new FileOutputStream(jsonName);
      outputStream.write(jsonBytes);
      outputStream.close();
      logger.info("Aggiornata tassonomia con successo.");
    } catch (ConnectException connException) {
      logger.error("Fallito tentativo di connessione.");
      throw new AppException(AppError.CONNECTION_REFUSED);
    } catch (FileNotFoundException fnfException) {
      logger.error("Fallito tentativo di recupero del file.");
      throw new AppException(AppError.FILE_DOES_NOT_EXIST);
    } catch (MalformedURLException muException) {
      throw new AppException(AppError.MALFORMED_URL);
    } catch (IOException ioException) {
      throw new AppException(AppError.ERROR_READING_WRITING);
    } catch (IllegalStateException isException) {
      throw new AppException(AppError.CSV_PARSING_ERROR);
    }
    catch(Exception e) {
      logger.error("Errore nell'aggiornamento.");
      //e.printStackTrace();
      throw new AppException(AppError.GENERATE_FILE);
    }
  }

  public List<? extends TaxonomyGeneric> getTaxonomyList(String version) {
    List<? extends TaxonomyGeneric> taxonomyGeneric;
    try {
      String taxonomy = new String(Files.readAllBytes(Paths.get(jsonName)), StandardCharsets.UTF_8);
      if(version.equalsIgnoreCase("standard")) {
        taxonomyGeneric = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectStandard>>(){});
      }
      else if(version.equalsIgnoreCase("datalake")){
        taxonomyGeneric = objectMapper.readValue(taxonomy, new TypeReference<List<TaxonomyObjectDatalake>>(){});
      } else {
        logger.error("La versione non esiste.");
        throw new AppException(AppError.VERSION_DOES_NOT_EXIST);
      }
      logger.info("Recuperata la versione della tassonomia con successo.");
      return taxonomyGeneric;
    } catch(NoSuchFileException nsf) {
      //nsf.printStackTrace();
      logger.error("Fallito tentativo di recupero del file.");
      throw new AppException(AppError.FILE_DOES_NOT_EXIST);
    } catch(Exception exc) {
      //exc.printStackTrace();
      logger.error("Errore interno del server.");
      throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }
  }
}
