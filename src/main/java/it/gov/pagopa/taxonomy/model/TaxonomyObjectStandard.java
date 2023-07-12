package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
public class TaxonomyObjectStandard extends TaxonomyObject {
  @JsonIgnore
  private String combinazioneTopicSubtopic;
  @JsonIgnore
  private String flagNuovaCombinazione;
}
