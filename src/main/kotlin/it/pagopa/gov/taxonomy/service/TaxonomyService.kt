package it.pagopa.gov.taxonomy.service

import it.pagopa.gov.taxonomy.enity.TaxonomyInfo
import it.pagopa.gov.taxonomy.exception.AppError
import it.pagopa.gov.taxonomy.exception.AppException
import it.pagopa.gov.taxonomy.repository.TaxonomyRepository
import lombok.extern.slf4j.Slf4j
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneId

@Service
@Slf4j
class TaxonomyService(private val taxonomyRepository: TaxonomyRepository) {
    val log: Logger = LoggerFactory.getLogger(TaxonomyService::class.java)

    fun getTaxonomy(code: String): TaxonomyInfo {
        return taxonomyRepository.findByCollectionData(code)
            .orElseThrow { AppException(AppError.NOT_FOUND, code) }
    }

    fun createTaxonomy(body: TaxonomyInfo): TaxonomyInfo {
        return taxonomyRepository.save(body)
    }

    fun getTaxonomies(code: String?): List<TaxonomyInfo> {
        return if (code == null) {
            taxonomyRepository.findAll().toList()
        } else {
            val element = getTaxonomy(code)
            listOf(element)
        }
    }

    fun createFromFile(file: MultipartFile): List<TaxonomyInfo> {
        val xlWb = WorkbookFactory.create(file.inputStream)
        val result = mutableListOf<TaxonomyInfo>()

        //Get reference to first sheet:
        val sheet = xlWb.getSheetAt(0)
        lateinit var macroArea: String;
        lateinit var macroAreaDescr: String;
        for (row in sheet) {
            if (row.rowNum != 0 && getCellValue(row.getCell(0)).isNotEmpty()) { // skip header and empty rows
                macroArea = row.getCell(3)?.stringCellValue ?: macroArea;
                macroAreaDescr = row.getCell(4)?.stringCellValue ?: macroAreaDescr;
                val taxonomyInfo = TaxonomyInfo(
                    creditorInstitutionTypeCode = getCellValue(row.getCell(0)),
                    creditorInstitutionType = getCellValue(row.getCell(1)),
                    progressiveMacroArea = getCellValue(row.getCell(2)),
                    macroAreaName = macroArea,
                    macroAreaDescription = macroAreaDescr,
                    serviceTypeCode = getCellValue(row.getCell(5)),
                    serviceType = getCellValue(row.getCell(6)),
                    legalReason = getCellValue(row.getCell(7)),
                    serviceDescription = getCellValue(row.getCell(8)),
                    version = getCellValue(row.getCell(9)),
                    collectionData = getCellValue(row.getCell(10)),
                    startValidityDate = row.getCell(11).dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    endValidityDate = row.getCell(12).dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                )
                result.add(taxonomyInfo)
            }
        }

        // save in to DB
        val res = taxonomyRepository.saveAll(result)
        return res
    }

    fun deleteTaxonomy(code: String) {
        val entity = getTaxonomy(code)
        taxonomyRepository.delete(entity)
    }

    private fun getCellValue(cell: Cell): String {
        return if (cell.cellType == CellType.STRING || cell.cellType == CellType.FORMULA) {
            cell.stringCellValue
        } else if (cell.cellType == CellType.NUMERIC) {
            cell.numericCellValue.toInt().toString()
        } else if (cell.cellType == CellType.BOOLEAN) {
            cell.booleanCellValue.toString()
        } else {
            ""
        }
    }


}
