package it.gov.pagopa.taxonomy.controller.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GenericMessage {
  private String message;
}
