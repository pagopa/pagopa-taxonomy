package it.gov.pagopa.microservice.controller;

import it.gov.pagopa.microservice.controller.response.GenericMessage;
import it.gov.pagopa.microservice.exception.AppError;
import it.gov.pagopa.microservice.exception.AppException;
import it.gov.pagopa.microservice.model.TaxonomyGeneric;
import it.gov.pagopa.microservice.service.TaxonomyService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TaxonomyController {
  @Value("${taxonomy.supportedExtensions}")
  String[] extensions;
  @Value("${taxonomy.availableVersions}")
  String[] versions;
  @Autowired
  TaxonomyService taxonomyService;
  private static final Logger logger = Logger.getLogger(TaxonomyController.class);
  @GetMapping("/generate")
  public GenericMessage updateTaxonomy() {
    taxonomyService.updateTaxonomy();
    return GenericMessage.builder().message("Taxonomy updated successfully.").build();
  }
  @GetMapping("/taxonomy.{ext}")
  public List<? extends TaxonomyGeneric> getTaxonomy(@PathVariable String ext, @RequestParam(value = "version",
          required = false, defaultValue = "standard") String ver) {
    List<String> supportedExtensions = Arrays.asList(extensions);
    List<String> availableVersions = Arrays.asList(versions);
    if(!supportedExtensions.contains(ext.toLowerCase()) || !availableVersions.contains(ver.toLowerCase())) {
      logger.error("L'estensione non è supportata o la versione non esiste.");
      throw new AppException(AppError.VERSION_DOES_NOT_EXIST);
    }
    return taxonomyService.getTaxonomyList(ver);
  }

}
