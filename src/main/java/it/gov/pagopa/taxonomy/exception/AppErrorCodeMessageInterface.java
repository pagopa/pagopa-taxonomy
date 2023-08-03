package it.gov.pagopa.taxonomy.exception;


import jakarta.ws.rs.core.Response;



public interface AppErrorCodeMessageInterface {
  String errorCode();

  String message(Object... args);

  Response.Status httpStatus();
}
