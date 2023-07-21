package it.gov.pagopa.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.project.model.TaxonomyObject;
import it.gov.pagopa.project.service.TaxonomyService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class TaxonomyServiceTest {
    @Test
    void getStandardJson() throws Exception {
        TaxonomyService taxonomyService = new TaxonomyService();
        List<TaxonomyObject> standardList = taxonomyService.getTaxonomyList("standard").getTaxonomyObjectList();
        String actual = new ObjectMapper().writeValueAsString(standardList.get(0));
        String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_standard.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
    @Test
    void getDatalakeJsonVersion() throws Exception {
        TaxonomyService taxonomyService = new TaxonomyService();
        List<TaxonomyObject> datalakeList = taxonomyService.getTaxonomyList("datalake").getTaxonomyObjectList();
        String actual = new ObjectMapper().writeValueAsString(datalakeList.get(0));
        String expected = Files.readString(Path.of("src/test/resources/response/get_taxonomy_datalake.json"));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}
