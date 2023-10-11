package it.gov.pagopa.taxonomy;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;


/**
 * Azure Functions with Azure Http trigger.
 */
public class Info {

    /**
     * This function will be invoked when a Http Trigger occurs
     *
     * @return
     */
    @FunctionName("FnHttpInfo")
    public HttpResponseMessage run(
            @HttpTrigger(name = "FnHttpInfoTrigger",
                    methods = {HttpMethod.GET},
                    route = "info",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return request.createResponseBuilder(HttpStatus.OK).build();
    }


}
