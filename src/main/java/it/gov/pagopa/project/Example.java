package it.gov.pagopa.project;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.project.service.TestService;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Optional;

public class Example {

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
