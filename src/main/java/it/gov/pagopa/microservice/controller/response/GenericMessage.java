package it.gov.pagopa.microservice.controller.response;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
public class GenericMessage {
  private String message;
}
