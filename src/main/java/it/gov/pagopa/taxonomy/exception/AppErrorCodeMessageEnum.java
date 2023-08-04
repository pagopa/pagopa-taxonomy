package it.gov.pagopa.taxonomy.exception;

import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;

import jakarta.ws.rs.core.Response;


public enum AppErrorCodeMessageEnum implements AppErrorCodeMessageInterface {

  // ERRORS
  ERROR("0500", "system.error", Response.Status.INTERNAL_SERVER_ERROR),
  CONNECTION_REFUSED("0100", "connection.refused", Response.Status.NOT_FOUND),
  FILE_DOES_NOT_EXIST("0101", "error.finding.file", Response.Status.NOT_FOUND),
  MALFORMED_URL("0102", "malformed.url", Response.Status.INTERNAL_SERVER_ERROR),
  CSV_PARSING_ERROR("0103", "parsing.error", Response.Status.INTERNAL_SERVER_ERROR),
  ERROR_READING_WRITING("0104", "ioexception", Response.Status.INTERNAL_SERVER_ERROR),
  MALFORMED_CSV("0105", "malformed.csv", Response.Status.INTERNAL_SERVER_ERROR),
  GENERATE_FILE("0106", "generate.error", Response.Status.INTERNAL_SERVER_ERROR);

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
