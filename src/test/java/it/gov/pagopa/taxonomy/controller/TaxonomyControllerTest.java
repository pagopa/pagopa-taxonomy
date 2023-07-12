package it.gov.pagopa.taxonomy.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(properties = { "taxonomy.jsonName=src/test/resources/taxonomy.json" })
@AutoConfigureMockMvc
class TaxonomyControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void contextLoads() {
    // check only if the context is loaded
    assertTrue(true);
  }

  // Google drive causing problems at times.
  @Test
  void generateTaxonomy() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/generate"))
    .andDo(print())
    .andExpect(status().isOk());
  }
  @Test
  void getStandardJson() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getStandardJsonVersion() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json?version=standard"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
  @Test
  void getDatalakeJsonVersion() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json?version=datalake"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getWrongFileExtension() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.md"))
            .andDo(print())
            .andExpect(status().isBadRequest());
  }
  @Test
  void getWrongFileVersion() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json?version=newversion"))
            .andDo(print())
            .andExpect(status().isBadRequest());
  }
  @Nested
  @TestPropertySource(properties = "taxonomy.csvLink=http://localhost:9090/file.csv")
  class TestWrongCsvLink {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void generateTaxonomyWrongUrl() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/generate"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
  }
  @Nested
  @TestPropertySource(properties = "taxonomy.jsonName=src/test/test/none.json")
  class TestWrongPath{
    @Autowired
    private MockMvc mockMvc;

    // Google drive causing problems at times.
    @Test
    void generateTaxonomyWrongPath() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/generate"))
      .andDo(print())
      .andExpect(status().isNotFound());
    }

    @Test
    void getJsonWrongPath() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json"))
              .andDo(print())
              .andExpect(status().isNotFound());
    }
  }
  @Nested
  @TestPropertySource(properties = "taxonomy.jsonName=src/test/resources/corrtaxonomy.json")
  class TestCorruptedJson {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void getJsonCorrupted() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/taxonomy.json"))
              .andDo(print())
              .andExpect(status().isInternalServerError());
    }
  }
}