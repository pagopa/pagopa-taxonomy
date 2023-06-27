package it.gov.pagopa.microservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxonomyObjectDatalake extends TaxonomyObjectStandard{
  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String combinazioneTopicSubtopic;
  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String flagNuovaCombinazione;
}
