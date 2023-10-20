package it.gov.pagopa.taxonomy;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import it.gov.pagopa.taxonomy.model.function.InfoMessage;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;


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
        Logger logger = context.getLogger();
        Properties properties = new Properties();
        String appVersion = null;
        String appName = null;
        InfoMessage infoMessage = new InfoMessage();
        try {
            properties.load(getClass().getResourceAsStream("/app.properties"));
            appVersion = properties.getProperty("app.version");
            appName = properties.getProperty("app.name");
        } catch (Exception e) {
            logger.log(Level.INFO, MessageFormat.format("Could not read app.properties at {0}\n {1}",
                Instant.now(), ExceptionUtils.getStackTrace(e)));
        }
        if(!appVersion.isEmpty() && appVersion != null && !appName.isEmpty() && appName != null) {
            infoMessage = InfoMessage.builder()
                .name(appName)
                .version(appVersion)
                .build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(infoMessage).build();
    }
}
