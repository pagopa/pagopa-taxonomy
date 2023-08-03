package it.gov.pagopa.project.exception;


import com.microsoft.azure.functions.HttpStatus;

import javax.ws.rs.core.Response;

public interface AppErrorCodeMessageInterface {
  String errorCode();

  String message(Object... args);

  Response.Status httpStatus();
}
