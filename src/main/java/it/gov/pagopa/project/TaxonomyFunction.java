package it.gov.pagopa.project;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.project.constants.Extension;
import it.gov.pagopa.project.constants.Version;
import it.gov.pagopa.project.exception.AppResponse;
import it.gov.pagopa.project.exception.ResponseMessage;
import it.gov.pagopa.project.model.TaxonomyObject;
import it.gov.pagopa.project.service.TaxonomyService;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
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
    ResponseMessage response = taxonomyService.updateTaxonomy().getResponse();

    return request.createResponseBuilder(response.getHttpStatus())
        .header("Content-Type", MediaType.APPLICATION_JSON)
        .body(response.getDetails())
        .build();
  }

  @FunctionName("GetTrigger")
  public HttpResponseMessage getTaxonomy(
          @HttpTrigger(
                  name = "GetTrigger",
                  methods = {HttpMethod.GET},
                  route = "taxonomy.{ext}",
                  authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
          @BindingName("ext") String extension,
      final ExecutionContext context) throws Exception {

    final String version = request.getQueryParameters().getOrDefault("version", "standard");

    if (!EnumUtils.isValidEnumIgnoreCase(Extension.class, extension) || !EnumUtils.isValidEnumIgnoreCase(Version.class, version)) {
      throw new Exception();
    }

    TaxonomyService taxonomyService = new TaxonomyService();
    AppResponse response = taxonomyService.getTaxonomyList(version);

    return request.createResponseBuilder(response.getResponse().getHttpStatus())
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .body(response.getResponse().getHttpStatus() == HttpStatus.OK ? response.getTaxonomyObjectList() : response.getResponse().getDetails())
            .build();
  }
}
