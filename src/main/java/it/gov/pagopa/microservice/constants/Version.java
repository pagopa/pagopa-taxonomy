package it.gov.pagopa.microservice.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Version {
    STANDARD("standard"),
    DATALAKE("datalake");

    private final String version;
}
