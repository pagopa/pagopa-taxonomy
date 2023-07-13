package it.gov.pagopa.taxonomy.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void getHome() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is3xxRedirection());
  }
}