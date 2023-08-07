package it.gov.pagopa.taxonomy.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = { "COMBINAZIONE TOPIC E SUBTOPIC", "FLAG NUOVA COMBINAZIONE" })
public class StandardTaxonomy extends Taxonomy{
}
