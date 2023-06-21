package it.gov.pagopa.microservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

@UtilityClass
public class Constants {


  public static final String HEADER_REQUEST_ID = "X-Request-Id";
  @Value("${taxonomy.csvName}")
  public static String csvName;

  @Value("${taxonomy.jsonName}")
  public static String jsonName;

}
