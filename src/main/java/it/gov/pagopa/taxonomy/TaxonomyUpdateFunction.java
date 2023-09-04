package it.gov.pagopa.taxonomy;

import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.taxonomy.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.function.ErrorMessage;
import it.gov.pagopa.taxonomy.model.function.Message;
import it.gov.pagopa.taxonomy.util.AppMessageUtil;
import it.gov.pagopa.taxonomy.util.AppUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyUpdateFunction {

  private static String msg = null;

  private static final String UPDATE_FAILED = AppMessageUtil.getMessage("update.failed");
  private static ObjectMapper objectMapper = null;

  private static ObjectMapper getObjectMapper(){
    if(objectMapper == null){
      objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
    }
    return objectMapper;
  }

  @FunctionName("FnHttpGenerate")
  public HttpResponseMessage updateTaxonomy(
      @HttpTrigger(
          name = "FnHttpGenerateTrigger",
          methods = {HttpMethod.GET},
          route = "generate",
          authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    Logger logger = context.getLogger();

    try {
      UpdateTaxonomy.updateTaxonomy(logger);
      String payload = AppUtil.getPayload(getObjectMapper(), Message.builder().message("Taxonomy updated successfully").build());
      logger.info("Taxonomy updated successfully");
      return AppUtil.writeResponse(request,
              HttpStatus.OK,
              payload
              );

    } catch (AppException e) {
      logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Update] AppException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
          .message(UPDATE_FAILED)
          .error(e.getCodeMessage().message(e.getArgs()))
          .build());
      return AppUtil.writeResponse(request,
          HttpStatus.valueOf(e.getCodeMessage().httpStatus().name()),
          payload
      );

    } catch (BlobStorageException e) {
      if(e.getErrorCode().equals(BlobErrorCode.BLOB_NOT_FOUND)) {
        logger.log(Level.SEVERE, MessageFormat.format("[ALERT][Update] BlobStorageException at {0} \n {1}" , Instant.now(), ExceptionUtils.getStackTrace(e)));

        AppException appException = new AppException(e, AppErrorCodeMessageEnum.BLOB_NOT_FOUND_CSV_ERROR);
        String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
            .message(UPDATE_FAILED)
            .error(appException.getCodeMessage().message(appException.getArgs()))
            .build());
        return AppUtil.writeResponse(request,
            HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
            payload
        );
      } else {
        logger.log(Level.SEVERE,MessageFormat.format( "[ALERT][Update] BlobStorageException at {0}\n {1}", Instant.now(), ExceptionUtils.getStackTrace(e)));
        AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
        String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
            .message(UPDATE_FAILED)
            .error(appException.getCodeMessage().message(appException.getArgs()))
            .build());
        return AppUtil.writeResponse(request,
            HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
            payload
        );
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE,MessageFormat.format("[ALERT][Update] Generic error at {0}\n {1}",Instant.now(), ExceptionUtils.getStackTrace(e)));

      AppException appException = new AppException(e, AppErrorCodeMessageEnum.ERROR);
      String payload = AppUtil.getPayload(getObjectMapper(), ErrorMessage.builder()
              .message(UPDATE_FAILED)
              .error(appException.getCodeMessage().message(appException.getArgs()))
              .build());
      return AppUtil.writeResponse(request,
              HttpStatus.valueOf(appException.getCodeMessage().httpStatus().name()),
              payload
              );
    }
  }
}
