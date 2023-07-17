package it.gov.pagopa.project;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.project.service.TestService;

import java.io.IOException;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Azure Functions with Azure Queue trigger.
 */
public class Example {

    /**
     * This function will be invoked when a Http Trigger occurs
     */
    @FunctionName("ServiceFunction")
    public HttpResponseMessage runService (
            @HttpTrigger(
                    name = "ServiceTrigger",
                    methods = {HttpMethod.GET},
                    route = "service",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws IOException {

        TestService testService = new TestService();

        testService.uploadFile();

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", MediaType.TEXT_PLAIN)
                .body("Service triggered!")
                .build();
    }
}
