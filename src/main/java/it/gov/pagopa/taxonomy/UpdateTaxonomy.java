//package it.gov.pagopa.project;
//
//import com.microsoft.azure.functions.*;
//import com.microsoft.azure.functions.annotation.AuthorizationLevel;
//import com.microsoft.azure.functions.annotation.FunctionName;
//import com.microsoft.azure.functions.annotation.HttpTrigger;
//import it.gov.pagopa.project.exception.ResponseMessage;
//import it.gov.pagopa.project.service.TaxonomyService;
//
//import javax.ws.rs.core.MediaType;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//public class UpdateTaxonomy {
//
//  @FunctionName("UpdateTaxonomyProcessor")
//  public HttpResponseMessage updateTaxonomy(
//      @HttpTrigger(
//          name = "UpdateTaxonomyTrigger",
//          methods = {HttpMethod.GET},
//          route = "generate",
//          authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
//      final ExecutionContext context) {
//
//    Logger logger = context.getLogger();
//
//    TaxonomyService taxonomyService = new TaxonomyService();
//    logger.info("call service taxonomy");
//    ResponseMessage response = taxonomyService.updateTaxonomy().getResponse();
//
//    logger.info("Done processing events");
//    return request.createResponseBuilder(response.getHttpStatus())
//        .header("Content-Type", MediaType.APPLICATION_JSON)
//        .body(response.getDetails())
//        .build();
//  }
//
//}
