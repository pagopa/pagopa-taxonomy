package it.gov.pagopa.taxonomy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AppUtil {

    public static<T> String getPayload(ObjectMapper objectMapper, T payload){
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return String.format("{ \"message\": \"%s\", \"error\" \"%s\"}", "Generic Taxonomy failed", e.getMessage());
        }
    }

    public static <T> HttpResponseMessage writeResponse(HttpRequestMessage<Optional<String>> request, HttpStatus httpStatus, String payload) {
        return writeResponseWithHeaders(request, httpStatus, payload, new LinkedHashMap<>());
    }
    public static HttpResponseMessage writeResponseWithHeaders(HttpRequestMessage<Optional<String>> request, HttpStatus httpStatus, String payload, Map<String, String> headers) {
        HttpResponseMessage.Builder responseBuilder = request.createResponseBuilder(httpStatus);
        responseBuilder.header("Content-Type", MediaType.APPLICATION_JSON);
        headers.forEach(responseBuilder::header);
        responseBuilder.body(payload);
        return responseBuilder.build();
    }

}
