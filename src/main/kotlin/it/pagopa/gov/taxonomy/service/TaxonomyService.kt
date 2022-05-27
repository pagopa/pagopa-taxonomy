package it.pagopa.gov.taxonomy.service

import it.pagopa.gov.taxonomy.enity.TaxonomyInfo
import it.pagopa.gov.taxonomy.repository.TaxonomyRepository
import org.springframework.stereotype.Service

@Service
class TaxonomyService(private val taxonomyRepository: TaxonomyRepository) {

    fun getTaxonomy(code: String): TaxonomyInfo {
        return taxonomyRepository.findByCollectionData(code)
            .orElseThrow()
    }

    fun createTaxonomy(body: TaxonomyInfo): TaxonomyInfo {
        return taxonomyRepository.save(body)
    }


}
