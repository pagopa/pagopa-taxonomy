package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaxonomyObjectStandard extends TaxonomyObject{
  @JsonIgnore
  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String dataInizioValidita;
  @JsonIgnore
  @JsonProperty(value="DATA FINE VALIDITA")
  private String dataFineValidita;
}
