package it.gov.pagopa.taxonomy.mapper;

import it.gov.pagopa.taxonomy.model.TaxonomyObject;
import it.gov.pagopa.taxonomy.model.TaxonomyObjectCsv;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaxonomyMapper {
  TaxonomyObject taxonomyCsvToTaxonomyObject(TaxonomyObjectCsv obj);

  List<TaxonomyObject> taxonomyCsvListToTaxonomyObjectList(List<TaxonomyObjectCsv> obj);

}