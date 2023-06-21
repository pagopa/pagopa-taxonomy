package it.gov.pagopa.microservice.mapper;

import it.gov.pagopa.microservice.model.TaxonomyObject;
import it.gov.pagopa.microservice.model.TaxonomyObjectCsv;
import it.gov.pagopa.microservice.model.TaxonomyObjectDatalake;
import it.gov.pagopa.microservice.model.TaxonomyObjectStandard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaxonomyMapper {
  TaxonomyObject taxonomyCsvToTaxonomyObject(TaxonomyObjectCsv obj);

  List<TaxonomyObject> taxonomyCsvListToTaxonomyObjectList(List<TaxonomyObjectCsv> obj);

}