package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(value = { "COMBINAZIONE TOPIC E SUBTOPIC", "FLAG NUOVA COMBINAZIONE" })
public class TaxonomyObjectStandard extends TaxonomyObject {
}
