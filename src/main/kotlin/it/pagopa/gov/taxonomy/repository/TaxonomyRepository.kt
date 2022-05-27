package it.pagopa.gov.taxonomy.repository

import it.pagopa.gov.taxonomy.enity.TaxonomyInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface TaxonomyRepository : JpaRepository<TaxonomyInfo, Long> {

    fun findByCollectionData(code: String): Optional<TaxonomyInfo>

}
