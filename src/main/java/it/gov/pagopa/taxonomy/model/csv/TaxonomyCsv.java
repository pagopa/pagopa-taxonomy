package it.gov.pagopa.taxonomy.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyCsv {
    @CsvBindByName(column = "CODICE TIPO ENTE CREDITORE", required = true)
    private String creditorEntityTypeCode;
    @CsvBindByName(column = "TIPO ENTE CREDITORE", required = true)
    private String creditorEntityType;
    @CsvBindByName(column = "# Progressivo Macro Area per Ente Creditore", required = true)
    private String creditorEntityMacroAreaProgressive;
    @CsvBindByName(column = "NOME MACRO AREA", required = true)
    private String macroAreaName;
    @CsvBindByName(column = "DESCRIZIONE MACRO AREA", required = true)
    private String macroAreaDescription;
    @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO", required = true)
    private String serviceTypologyCode;
    @CsvBindByName(column = "TIPO SERVIZIO", required = true)
    private String serviceType;
    @CsvBindByName(column = "Motivo Giuridico della riscossione", required = true)
    private String legalBasisOfCollection;
    @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO", required = true)
    private String serviceTypeDescription;
    @CsvBindByName(column = "# VERSIONE TASSONOMIA", required = true)
    private String taxonomyVersion;
    @CsvBindByName(column = "DATI SPECIFICI DI INCASSO", required = true)
    private String specificCollectionData;
    @CsvBindByName(column = "DATA INIZIO VALIDITA (mm/gg/aaaa)", required = true)
    private String validityStartDate;
    @CsvBindByName(column = "DATA FINE VALIDITA (mm/gg/aaaa)", required = true)
    private String validityEndDate;
    @CsvBindByName(column = "combinazione topic&subtopic", required = true)
    private String topicSubtopicCombination;
    @CsvBindByName(column = "Flag_Nuova_Combinazione", required = true)
    private String newCombinationFlag;
}
