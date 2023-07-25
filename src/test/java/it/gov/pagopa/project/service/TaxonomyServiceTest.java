package it.gov.pagopa.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpStatus;
import it.gov.pagopa.project.exception.AppResponse;
import it.gov.pagopa.project.model.TaxonomyObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaxonomyServiceTest {

    static TaxonomyService taxonomyService;

    @BeforeAll
    public static void init() {
        taxonomyService = new TaxonomyService();
    }

    @Test
    @Order(0)
    void generateJson() {
        try {
            AppResponse appResponse = taxonomyService.updateTaxonomy();
            Assertions.assertEquals(HttpStatus.OK, appResponse.getResponse().getHttpStatus());
        } catch(Exception exc) {
            Assertions.fail();
        }
    }

    @Test
    void getStandardJson() throws Exception {
        List<TaxonomyObject> standardList = taxonomyService.getTaxonomyList("standard").getTaxonomyObjectList();
        String actual = new ObjectMapper().writeValueAsString(standardList.get(0));
        String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_standard.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getDatalakeJsonVersion() throws Exception {
        List<TaxonomyObject> datalakeList = taxonomyService.getTaxonomyList("datalake").getTaxonomyObjectList();
        String actual = new ObjectMapper().writeValueAsString(datalakeList.get(0));
        String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_datalake.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCsvWrongHeaderName() throws Exception {
        URL urlTest = new File("src/test/resources/csv/wrong_column_test.csv").toURI().toURL();
        String URL = urlTest.toString();
        Field stringUrlField = taxonomyService.getClass().getDeclaredField("stringUrl");
        stringUrlField.setAccessible(true);
        stringUrlField.set(taxonomyService, URL);
        try {
            AppResponse appResponse = taxonomyService.updateTaxonomy();
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appResponse.getResponse().getHttpStatus());
        } catch(Exception exc) {
            Assertions.fail();
        }
    }

    @Test
    void getCsvNullColumns() throws Exception {
        URL urlTest = new File("src/test/resources/csv/null_column_test.csv").toURI().toURL();
        String URL = urlTest.toString();
        Field stringUrlField = taxonomyService.getClass().getDeclaredField("stringUrl");
        stringUrlField.setAccessible(true);
        stringUrlField.set(taxonomyService, URL);
        try {
            AppResponse appResponse = taxonomyService.updateTaxonomy();
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appResponse.getResponse().getHttpStatus());
        } catch(Exception exc) {
            Assertions.fail();
        }
    }

    @Test
    void getCsvNullField() throws Exception {
        URL urlTest = new File("src/test/resources/csv/null_field_test.csv").toURI().toURL();
        String URL = urlTest.toString();
        Field stringUrlField = taxonomyService.getClass().getDeclaredField("stringUrl");
        stringUrlField.setAccessible(true);
        stringUrlField.set(taxonomyService, URL);
        try {
            AppResponse appResponse = taxonomyService.updateTaxonomy();
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appResponse.getResponse().getHttpStatus());
        } catch(Exception exc) {
            Assertions.fail();
        }
    }
    @Test
    void getCorruptedJson() throws FileNotFoundException, NoSuchFieldException, IllegalAccessException {
        FileReader reader = new FileReader("src/test/resources/json/corrupted.json");
        TaxonomyService taxonomyService1 = new TaxonomyService();
        Field jsonStringField = taxonomyService1.getClass().getDeclaredField("jsonString");
        jsonStringField.setAccessible(true);
        jsonStringField.set(taxonomyService1, reader.toString());
        Field isTestField = taxonomyService1.getClass().getDeclaredField("isTest");
        isTestField.setAccessible(true);
        isTestField.set(taxonomyService1, true);
        try {
            AppResponse appResponse = taxonomyService1.getTaxonomyList("standard");
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appResponse.getResponse().getHttpStatus());
        } catch (Exception exc) {
            Assertions.fail();
        }
    }
}
