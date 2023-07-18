package it.gov.pagopa.project.exception;

import it.gov.pagopa.project.model.TaxonomyObject;
import java.util.List;

public class AppResponse {
  private ResponseMessage response;
  private List<TaxonomyObject> taxonomyObjectList;

  public AppResponse(ResponseMessage responseMessage) {
    this.response= responseMessage;
  }
  public AppResponse(ResponseMessage responseMessage, List<TaxonomyObject> taxonomyObjectList) {
    this.response= responseMessage;
    this.taxonomyObjectList = taxonomyObjectList;
  }

  public ResponseMessage getResponse() {
    return response;
  }

  public List<TaxonomyObject> getTaxonomyObjectList() {
    return taxonomyObjectList;
  }
}
