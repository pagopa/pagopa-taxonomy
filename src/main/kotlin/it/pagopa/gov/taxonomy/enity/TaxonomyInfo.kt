package it.pagopa.gov.taxonomy.enity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.Hibernate
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Pattern

@Entity
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaxonomyInfo(

    // use collectionData as ID

    @JsonProperty(value = "CODICE TIPO ENTE CREDITORE")
    @Column(name = "creditor_institution_type_code")
    @Schema(description = "CODICE TIPO ENTE CREDITORE", example = "01", format = "integer", required = true)
    @Pattern(regexp = "\\d+")
    val creditorInstitutionTypeCode: String,

    @JsonProperty(value = "TIPO ENTE CREDITORE")
    @Column(name = "creditor_institution_type")
    @Schema(description = "TIPO ENTE CREDITORE", required = true)
    val creditorInstitutionType: String,

    @JsonProperty(value = "Progressivo Macro Area per Ente Creditore")
    @Column(name = "progressive_macro_area")
    @Schema(
        description = "Progressivo Macro Area per Ente Creditore",
        example = "01",
        format = "integer",
        required = true
    )
    @Pattern(regexp = "\\d+")
    val progressiveMacroArea: String,

    @JsonProperty(value = "NOME MACRO AREA")
    @Column(name = "macro_area_name")
    @Schema(description = "NOME MACRO AREA", required = true)
    val macroAreaName: String,

    @JsonProperty(value = "DESCRIZIONE MACRO AREA")
    @Column(name = "macro_area_description", columnDefinition = "TEXT")
    @Schema(description = "DESCRIZIONE MACRO AREA", required = true)
    val macroAreaDescription: String,

    @JsonProperty(value = "CODICE TIPOLOGIA SERVIZIO")
    @Column(name = "service_type_code")
    @Schema(description = "CODICE TIPOLOGIA SERVIZIO", example = "100", format = "integer", required = true)
    @Pattern(regexp = "\\d+")
    val serviceTypeCode: String,

    @JsonProperty(value = "TIPO SERVIZIO")
    @Column(name = "service_type")
    @Schema(description = "TIPO SERVIZIO", required = true)
    val serviceType: String,

    @JsonProperty(value = "Motivo Giuridico della riscossione")
    @Column(name = "legal_reason")
    @Schema(description = "Motivo Giuridico della riscossione", required = true)
    val legalReason: String,

    @JsonProperty(value = "DESCRIZIONE TIPO SERVIZIO")
    @Column(name = "service_description", columnDefinition = "TEXT")
    @Schema(description = "DESCRIZIONE TIPO SERVIZIO", required = true)
    val serviceDescription: String,

    @JsonProperty(value = "VERSIONE TASSONOMIA")
    @Column(name = "version")
    @Schema(description = "VERSIONE TASSONOMIA", example = "1", format = "integer", required = true)
    @Pattern(regexp = "\\d+")
    val version: String,

    @JsonProperty(value = "DATI SPECIFICI DI INCASSO")
    @Column(name = "collection_data", unique = true, nullable = false)
    @Id
    @Schema(description = "DATI SPECIFICI DI INCASSO", required = true)
    val collectionData: String,

    @JsonProperty(value = "DATA INIZIO VALIDITA")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "start_validity_date")
    @Schema(
        description = "DATA INIZIO VALIDITA",
        example = "25/07/2022",
        type = "string",
        pattern = "dd/MM/yyyy",
        required = true
    )
    val startValidityDate: LocalDate,

    @JsonProperty(value = "DATA FINE VALIDITA")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "end_validity_date")
    @Schema(
        description = "DATA FINE VALIDITA",
        example = "25/07/2022",
        type = "string",
        pattern = "dd/MM/yyyy",
        required = true
    )
    val endValidityDate: LocalDate,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaxonomyInfo

        return collectionData == other.collectionData
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(collectionData = $collectionData )"
    }

}
