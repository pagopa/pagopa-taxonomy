package it.gov.pagopa.project.exception;

import com.microsoft.azure.functions.HttpStatus;
import lombok.Getter;

@Getter
public enum ResponseMessage {
  // ERRORS
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
      "Something was wrong"),
  VERSION_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST, "Error requesting file", "Version or Extension are not supported"),
  GENERATE_FILE(HttpStatus.BAD_REQUEST, "Error generating file", "Could not generate file"),
  FILE_DOES_NOT_EXIST(HttpStatus.NOT_FOUND, "Error finding file", "Cannot read CSV file or write JSON"),
  CONNECTION_REFUSED(HttpStatus.NOT_FOUND, "Connection Refused", "Could not connect to host"),
  MALFORMED_URL(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed URL", "The CSV file url provided is malformed"),
  ERROR_READING_WRITING(HttpStatus.INTERNAL_SERVER_ERROR, "IOException", "Error when reading or writing the file"),
  CSV_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing Error", "Error when parsing the CSV file"),
  MALFORMED_CSV(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed CSV file", "CSV file is malformed."),
  JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing Error", "Error when parsing the JSON file, (file could be empty or corrupted, generate a new one)"),
  JSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Json not found", "Error finding JSON file."),
  // ERRORS

  // OK
  TAXONOMY_UPDATED(HttpStatus.OK, "Updated", "Taxonomy updated successfully."),
  TAXONOMY_RETRIEVED(HttpStatus.OK, "Retrieved", "Taxonomy retrieved successfully.");
  // OK


  public final HttpStatus httpStatus;
  public final String title;
  public final String details;

  ResponseMessage(HttpStatus httpStatus, String title, String details) {
    this.httpStatus = httpStatus;
    this.title = title;
    this.details = details;
  }

}


