package it.gov.pagopa.taxonomy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.gov.pagopa.taxonomy.controller.response.GenericMessage;
import it.gov.pagopa.taxonomy.exception.AppError;
import it.gov.pagopa.taxonomy.exception.AppException;
import it.gov.pagopa.taxonomy.model.TaxonomyObjectDatalake;
import it.gov.pagopa.taxonomy.model.TaxonomyObjectStandard;
import it.gov.pagopa.taxonomy.service.TaxonomyService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TaxonomyController {

  @Value("${taxonomy.supportedExtensions}")
  String extensions;

  @Value("${taxonomy.availableVersions}")
  String versions;

  @Autowired
  TaxonomyService taxonomyService;

  private static final Logger logger = Logger.getLogger(TaxonomyController.class);
  @Operation(
          summary = "Generates taxonomy JSON file")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "OK"),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Not Found, cannot access CSV file."),
                  @ApiResponse(
                          responseCode = "500",
                          description = "Service unavailable")
          })
  @GetMapping("/generate")
  public GenericMessage updateTaxonomy() {
    taxonomyService.updateTaxonomy();
    return GenericMessage.builder().message("Taxonomy updated successfully.").build();
  }
  @Operation(
          summary = "Get Taxonomy")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "OK",
                          content =
                          @Content(
                                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(oneOf = { TaxonomyObjectStandard.class, TaxonomyObjectDatalake.class } ))),
                  @ApiResponse(
                          responseCode = "400",
                          description = "Bad Request, file extension or version do not exist.",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Not Found, JSON file not found.",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                  @ApiResponse(
                          responseCode = "500",
                          description = "Service unavailable",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
          })
  @GetMapping("/taxonomy.{ext}")
  public List<? extends TaxonomyObjectStandard>getTaxonomy(@PathVariable("ext")
              @Parameter(name = "ext", description = "File extension", example = "json") String ext,
              @RequestParam(value = "version",
              required = false,
              defaultValue = "standard") String ver) {

    List<String> supportedExtensions = Arrays.asList(extensions.split(","));
    List<String> availableVersions = Arrays.asList(versions.split(","));
    if (!supportedExtensions.contains(ext.toLowerCase()) || !availableVersions.contains(ver.toLowerCase())) {
      logger.error("The extension is not supported or the version does not exist.");
      throw new AppException(AppError.VERSION_DOES_NOT_EXIST);
    }
    return taxonomyService.getTaxonomyList(ver);
  }
}
