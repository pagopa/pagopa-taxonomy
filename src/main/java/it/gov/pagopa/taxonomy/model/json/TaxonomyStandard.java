package it.gov.pagopa.taxonomy.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"COMBINAZIONE TOPIC E SUBTOPIC"})
public class TaxonomyStandard extends Taxonomy {
}
