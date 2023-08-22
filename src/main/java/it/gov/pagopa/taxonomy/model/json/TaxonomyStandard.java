package it.gov.pagopa.taxonomy.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"COMBINAZIONE TOPIC E SUBTOPIC", "FLAG NUOVA COMBINAZIONE"})
public class TaxonomyStandard extends Taxonomy {
}
