package it.gov.pagopa.taxonomy.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

@UtilityClass
public class Constants {


  public static final String HEADER_REQUEST_ID = "X-Request-Id";

  @Value("${taxonomy.jsonName}")
  public static final String jsonName = null;

}
