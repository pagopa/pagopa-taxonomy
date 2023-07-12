package it.gov.pagopa.taxonomy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.TaxonomyObject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = { "taxonomy.jsonName=src/test/resources/taxonomy.json" })
class TaxonomyServiceTest {
  @Autowired TaxonomyService taxonomyService;

  @Test
  void getStandardJson() throws Exception {
    List<TaxonomyObject> standardList = taxonomyService.getTaxonomyList("standard");
    String actual = new ObjectMapper().writeValueAsString(standardList.get(0));
    String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_standard.json"));
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
  @Test
  void getDatalakeJsonVersion() throws Exception {
      List<TaxonomyObject> datalakeList = taxonomyService.getTaxonomyList("datalake");
      String actual = new ObjectMapper().writeValueAsString(datalakeList.get(0));
      String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_datalake.json"));
      JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
    @Nested
    class TestWrongPath{
        @Test
        void getJsonWrongPath() {
          TaxonomyService taxonomyServiceTest = new TaxonomyService();
          String jsonName = "src/test/test/none.json";
          ReflectionTestUtils.setField(taxonomyServiceTest, "jsonName", jsonName);
            try {
                taxonomyServiceTest.getTaxonomyList("standard");
                fail();
            }catch(AppException exc) {
                assertEquals(HttpStatus.NOT_FOUND, exc.getHttpStatus());
            }catch(Exception exc) {
                fail();
            }
        }
    }

    @Nested
    class TestParsingError{
        @Test
        void getJsonParsingError(){
            TaxonomyService taxonomyServiceTest = new TaxonomyService();
            String jsonName = "taxonomy.jsonName=src/test/resources/corrtaxonomy.json";
            ReflectionTestUtils.setField(taxonomyServiceTest, "jsonName", jsonName);
            try {
                taxonomyServiceTest.getTaxonomyList("standard");
                fail();
            }catch(AppException exc) {
                assertEquals(HttpStatus.NOT_FOUND, exc.getHttpStatus());
            }catch(Exception exc) {
                fail();
            }
        }
    }
  @Nested
  class TestWrongHeaderName{
    @Test
    void getCsvWrongHeaderName() throws MalformedURLException {
      TaxonomyService taxonomyServiceTest = new TaxonomyService();
      URL urlTest = new File("src/test/resources/csv/wrong_column_test.csv").toURI().toURL();
      String URL = urlTest.toString();
      ReflectionTestUtils.setField(taxonomyServiceTest, "stringUrl", URL);
      try {
        taxonomyServiceTest.updateTaxonomy();
        fail();
      }catch(AppException exc) {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exc.getHttpStatus());
      }catch(Exception exc) {
        fail();
      }
    }
  }
  @Nested
  class TestNullColumn{
    @Test
    void getCsvNullColumns() throws MalformedURLException {
      TaxonomyService taxonomyServiceTest = new TaxonomyService();
      URL urlTest = new File("src/test/resources/csv/null_column_test.csv").toURI().toURL();
      String URL = urlTest.toString();
      ReflectionTestUtils.setField(taxonomyServiceTest, "stringUrl", URL);
      try {
        taxonomyServiceTest.updateTaxonomy();
        fail();
      }catch(AppException exc) {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exc.getHttpStatus());
      }catch(Exception exc) {
        fail();
      }
    }
  }
  @Nested
  class TestNullField{
    @Test
    void getCsvNullField() throws MalformedURLException {
      TaxonomyService taxonomyServiceTest = new TaxonomyService();
      URL urlTest = new File("src/test/resources/csv/null_field_test.csv").toURI().toURL();
      String URL = urlTest.toString();
      ReflectionTestUtils.setField(taxonomyServiceTest, "stringUrl", URL);
      try {
        taxonomyServiceTest.updateTaxonomy();
        fail();
      }catch(AppException exc) {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exc.getHttpStatus());
      }catch(Exception exc) {
        fail();
      }
    }
  }
}