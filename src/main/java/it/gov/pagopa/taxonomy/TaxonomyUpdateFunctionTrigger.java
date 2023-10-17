package it.gov.pagopa.taxonomy;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import it.gov.pagopa.taxonomy.exception.AppException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyUpdateFunctionTrigger {
    @FunctionName("FnBlobTriggerGenerate")
    public void run(
            @BlobTrigger(name = "file",
                    dataType = "binary",
                    path = "%BLOB_CONTAINER_NAME_INPUT%/%CSV_NAME%",
                    connection = "STORAGE_ACCOUNT_CONN_STRING") byte[] content,
            final ExecutionContext context
    ) {
        Logger logger = context.getLogger();

        try {
            UpdateTaxonomy.updateTaxonomy(logger);
            logger.info("Taxonomy updated successfully");

        } catch (AppException e) {
            logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Update-Triggered] AppException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Update-Triggered] Generic error at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));

        }
    }
}
