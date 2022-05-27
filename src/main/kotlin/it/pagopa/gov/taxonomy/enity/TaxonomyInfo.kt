package it.pagopa.gov.taxonomy.enity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.DateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer
import it.pagopa.gov.taxonomy.service.TaxonomyService
import lombok.EqualsAndHashCode
import lombok.ToString
import java.text.DateFormat
import java.util.Date
import javax.persistence.*

@Entity
@Table
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaxonomyInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @JsonProperty(value = "CODICE TIPO ENTE CREDITORE")
    @Column(
        name = "creditor_institution_type_code"
    )
    val creditorInstitutionTypeCode: String?,

    @JsonProperty(value = "TIPO ENTE CREDITORE")
    @Column(
        name = "creditor_institution_type"
    )
    val creditorInstitutionType: String?,

    @JsonProperty(value = "Progressivo Macro Area per Ente Creditore")
    @Column(
        name = "progressive_macro_area"
    )
    val progressiveMacroArea: String?,

    @JsonProperty(value = "NOME MACRO AREA")
    @Column(
        name = "macro_area_name"
    )
    val macroAreaName: String?,

    @JsonProperty(value = "DESCRIZIONE MACRO AREA")
    @Column(
        name = "macro_area_description"
    )
    val macroAreaDescription: String?,

    @JsonProperty(value = "CODICE TIPOLOGIA SERVIZIO")
    @Column(
        name = "service_type_code"
    )
    val serviceTypeCode: String?,

    @JsonProperty(value = "TIPO SERVIZIO")
    @Column(
        name = "service_type"
    )
    val serviceType: String?,

    @JsonProperty(value = "Motivo Giuridico della riscossione")
    @Column(
        name = "legal_reason"
    )
    val legalReason: String?,

    @JsonProperty(value = "DESCRIZIONE TIPO SERVIZIO")
    @Column(
        name = "service_description"
    )
    val serviceDescription: String?,

    @JsonProperty(value = "VERSIONE TASSONOMIA")
    @Column(
        name = "taxonomy_service"
    )
    val taxonomyService: String?,

    @JsonProperty(value = "DATI SPECIFICI DI INCASSO")
    @Column(
        name = "collection_data", unique = true, nullable = false
    )
    val collectionData: String,

    @JsonProperty(value = "DATA INIZIO VALIDITA")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonSerialize(using = DateSerializer::class)
    @Column(
        name = "start_validity_date"
    )
    val startValidityDate: Date?,

    @JsonProperty(value = "DATA FINE VALIDITA")
    @Column(
        name = "end_validity_date"
    )
    val endValidityDate: Date?,


    )
