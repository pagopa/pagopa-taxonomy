package it.gov.pagopa.project;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.project.service.TaxonomyService;
import it.gov.pagopa.project.service.TestService;
import java.io.IOException;
import java.util.Optional;
import javax.ws.rs.core.MediaType;

public class TaxonomyFunction {
  @FunctionName("UpdateTrigger")
  public HttpResponseMessage updateTaxonomy(
      @HttpTrigger(
          name = "UpdateTrigger",
          methods = {HttpMethod.GET},
          route = "generate",
          authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

    TaxonomyService taxonomyService = new TaxonomyService();
    taxonomyService.updateTaxonomy();

    return request.createResponseBuilder(HttpStatus.OK)
        .header("Content-Type", MediaType.TEXT_PLAIN)
        .body("Taxonomy Updated!")
        .build();
  }
}
