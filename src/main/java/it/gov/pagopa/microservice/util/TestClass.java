package it.gov.pagopa.microservice.util;

import com.opencsv.bean.processor.StringProcessor;
import org.apache.commons.lang3.StringUtils;

public class TestClass implements StringProcessor {
  String defaultValue;

  @Override
  public String processString(String value) {
    if (StringUtils.isNotEmpty(value)) {
      return value.trim();
    }
    return value;
  }

  @Override
  public void setParameterString(String value) {
    defaultValue = value;
  }
}
