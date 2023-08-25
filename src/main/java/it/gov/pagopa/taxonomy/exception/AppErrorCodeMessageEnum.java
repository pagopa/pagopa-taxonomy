package it.gov.pagopa.taxonomy.exception;

import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


public enum AppErrorCodeMessageEnum implements AppErrorCodeMessageInterface {

  // ERRORS
  ERROR("0500", "system.error", Response.Status.INTERNAL_SERVER_ERROR),
  BLOB_NOT_FOUND_CSV_ERROR("0404", "blob.not.found.csv.error", Status.NOT_FOUND),
  BLOB_NOT_FOUND_JSON_ERROR("0404", "blob.not.found.json.error", Status.NOT_FOUND),
  CSV_PARSING_ERROR("0103", "csv.parsing.error", Response.Status.INTERNAL_SERVER_ERROR),
  JSON_PARSING_ERROR("0107", "json.parsing.error", Response.Status.INTERNAL_SERVER_ERROR);


  private final String errorCode;
  private final String errorMessageKey;
  private final Response.Status httpStatus;

  AppErrorCodeMessageEnum(
      String errorCode, String errorMessageKey, Response.Status httpStatus) {
    this.errorCode = errorCode;
    this.errorMessageKey = errorMessageKey;
    this.httpStatus = httpStatus;
  }

  @Override
  public String errorCode() {
    return AppConstant.SERVICE_CODE_APP + "-" + errorCode;
  }

  @Override
  public String message(Object... args) {
    return AppMessageUtil.getMessage(errorMessageKey, args);
  }

  @Override
  public Response.Status httpStatus() {
    return httpStatus;
  }
}
