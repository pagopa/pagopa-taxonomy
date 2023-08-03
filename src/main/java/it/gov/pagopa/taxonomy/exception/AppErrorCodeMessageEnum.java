package it.gov.pagopa.taxonomy.exception;

import it.gov.pagopa.taxonomy.util.AppConstant;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;

import jakarta.ws.rs.core.Response;


public enum AppErrorCodeMessageEnum implements AppErrorCodeMessageInterface {

  // ERRORS
  ERROR("0500", "system.error", Response.Status.INTERNAL_SERVER_ERROR),
  CONNECTION_REFUSED("0100", "connection.refused", Response.Status.NOT_FOUND),;
//  VERSION_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST, "Error requesting file", "Version or Extension are not supported."),
//  GENERATE_FILE(HttpStatus.BAD_REQUEST, "Error generating file", "Could not generate file."),
//  FILE_DOES_NOT_EXIST(HttpStatus.NOT_FOUND, "Error finding file", "Cannot read CSV file or write JSON."),
//  CONNECTION_REFUSED(HttpStatus.NOT_FOUND, "Connection Refused", "Could not connect to host."),
//  MALFORMED_URL(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed URL", "The CSV file url provided is malformed."),
//  ERROR_READING_WRITING(HttpStatus.INTERNAL_SERVER_ERROR, "IOException", "Error when reading or writing the file."),
//  CSV_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing Error", "Error when parsing the CSV file."),
//  MALFORMED_CSV(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed CSV file", "CSV file is malformed."),
//  JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing Error", "Error when parsing the JSON file, (file could be empty or corrupted, generate a new one)."),
//  JSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Json not found", "Error finding JSON file."),
//  // ERRORS
//
//  // OK
//  TAXONOMY_UPDATED(HttpStatus.OK, "Updated", "Taxonomy updated successfully."),
//  TAXONOMY_RETRIEVED(HttpStatus.OK, "Retrieved", "Taxonomy retrieved successfully.");
//  // OK


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
