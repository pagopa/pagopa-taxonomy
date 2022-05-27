package it.pagopa.gov.taxonomy.controller

import it.pagopa.gov.taxonomy.enity.TaxonomyInfo
import it.pagopa.gov.taxonomy.service.TaxonomyService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/taxonomy")
class TaxonomyController(private val taxonomyService: TaxonomyService) {

    @GetMapping("/{code}")
    fun getTaxonomyInfo(@PathVariable("code") code: String): TaxonomyInfo {
        return taxonomyService.getTaxonomy(code);
    }

    @PostMapping
    fun createTaxonomyInfo(@RequestBody body: TaxonomyInfo): TaxonomyInfo {
        return taxonomyService.createTaxonomy(body);
    }


}
