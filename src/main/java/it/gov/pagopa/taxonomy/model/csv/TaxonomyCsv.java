package it.gov.pagopa.taxonomy.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyCsv {
  @CsvBindByName(column = "CODICE TIPO ENTE CREDITORE", required = true)
  private String creditorEntityTypeCode;
  @CsvBindByName(column = "TIPO ENTE CREDITORE", required = true)
  private String creditorEntityType;
  @CsvBindByName(column = "PROGRESSIVO MACRO AREA PER ENTE CREDITORE", required = true)
  private String creditorEntityMacroAreaProgressive;
  @CsvBindByName(column = "NOME MACRO AREA", required = true)
  private String macroAreaName;
  @CsvBindByName(column = "DESCRIZIONE MACRO AREA", required = true)
  private String macroAreaDescription;
  @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO", required = true)
  private String serviceTypologyCode;
  @CsvBindByName(column = "TIPO SERVIZIO", required = true)
  private String serviceType;
  @CsvBindByName(column = "MOTIVO GIURIDICO DELLA RISCOSSIONE", required = true)
  private String legalBasisOfCollection;
  @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO", required = true)
  private String serviceTypeDescription;
  @CsvBindByName(column = "VERSIONE TASSONOMIA", required = true)
  private String taxonomyVersion;
  @CsvBindByName(column = "DATI SPECIFICI DI INCASSO", required = true)
  private String specificCollectionData;
  @CsvBindByName(column = "DATA INIZIO VALIDITA", required = true)
  private String validityStartDate;
  @CsvBindByName(column = "DATA FINE VALIDITA", required = true)
  private String validityEndDate;
  /*
  @CsvBindByName(column = "COMBINAZIONE TOPIC E SUBTOPIC", required = true)
  private String topicSubtopicCombination;
  @CsvBindByName(column = "FLAG NUOVA COMBINAZIONE", required = true)
  private String newCombinationFlag; */
}
