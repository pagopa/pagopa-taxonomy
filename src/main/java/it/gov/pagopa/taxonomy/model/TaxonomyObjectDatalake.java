package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaxonomyObjectDatalake extends TaxonomyObjectStandard{
  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String combinazioneTopicSubtopic;
  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String flagNuovaCombinazione;
}
