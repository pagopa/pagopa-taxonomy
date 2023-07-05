package it.gov.pagopa.taxonomy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.taxonomy.constants.Version;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.TaxonomyObjectStandard;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "taxonomy.jsonName=src/test/resources/taxonomy.json" })
@AutoConfigureMockMvc

class ServiceTest {
  @Autowired TaxonomyService taxonomyService;

  @Test
  void getStandardJson() throws Exception {
    List<? extends TaxonomyObjectStandard> standardList = taxonomyService.getTaxonomyList(Version.STANDARD.toString());
    String actual = new ObjectMapper().writeValueAsString(standardList.get(0));
    String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_standard.json"));
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
  @Test
  void getDatalakeJsonVersion() throws Exception {
      List<? extends TaxonomyObjectStandard> datalakeList = taxonomyService.getTaxonomyList(Version.DATALAKE.toString());
      String actual = new ObjectMapper().writeValueAsString(datalakeList.get(0));
      String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_datalake.json"));
      JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
    @Nested
    @TestPropertySource(properties = "taxonomy.jsonName=src/test/test/none.json")
    public class TestWrongPath{
        @Test
        void getJsonWrongPath() {
            try {
                List<? extends TaxonomyObjectStandard> standardList = taxonomyService.getTaxonomyList(Version.STANDARD.toString());
            }catch(AppException exc) {
                assertEquals(HttpStatus.NOT_FOUND, exc.getHttpStatus());
            }catch(Exception exc) {
                fail();
            }
        }
    }

    @Nested
    @TestPropertySource(properties = "taxonomy.jsonName=src/test/resources/corrtaxonomy.json")
    public class TestParsingError{
        @Test
        void getJsonParsingError(){
            try {
                List<? extends TaxonomyObjectStandard> standardList = taxonomyService.getTaxonomyList(Version.STANDARD.toString());
            }catch(AppException exc) {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exc.getHttpStatus());
            }catch(Exception exc) {
                fail();
            }
        }
    }
}