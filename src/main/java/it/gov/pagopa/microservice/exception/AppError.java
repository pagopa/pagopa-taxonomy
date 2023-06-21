package it.gov.pagopa.microservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum AppError {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
      "Something was wrong"),
  VERSION_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST, "Error requesting file", "Version or Extension are not supported"),
  GENERATE_FILE(HttpStatus.BAD_REQUEST, "Error generating file", "Could not generate file"),
  FILE_DOES_NOT_EXIST(HttpStatus.NOT_FOUND, "Error finding file", "File does not exist"),
  CONNECTION_REFUSED(HttpStatus.NOT_FOUND, "Connection Refused", "Could not connect to host"),
  MALFORMED_URL(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed URL", "The CSV file url provided is malformed"),
  ERROR_READING_WRITING(HttpStatus.INTERNAL_SERVER_ERROR, "IOException", "Error when reading or writing the file"),
  CSV_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing Error", "Error when parsing the CSV file");


  public final HttpStatus httpStatus;
  public final String title;
  public final String details;


  AppError(HttpStatus httpStatus, String title, String details) {
    this.httpStatus = httpStatus;
    this.title = title;
    this.details = details;
  }
}


