package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TaxonomyObject {
  @CsvBindByName(column = "CODICE TIPO ENTE CREDITORE", required = true)
  @JsonProperty(value="CODICE TIPO ENTE CREDITORE")
  private String creditorEntityTypeCode;
  @CsvBindByName(column = "TIPO ENTE CREDITORE", required = true)
  @JsonProperty(value="TIPO ENTE CREDITORE")
  private String creditorEntityType;
  @CsvBindByName(column = "PROGRESSIVO MACRO AREA PER ENTE CREDITORE", required = true)
  @JsonProperty(value="PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String creditorEntityMacroAreaProgressive;
  @CsvBindByName(column = "NOME MACRO AREA", required = true)
  @JsonProperty(value="NOME MACRO AREA")
  private String macroAreaName;
  @CsvBindByName(column = "DESCRIZIONE MACRO AREA", required = true)
  @JsonProperty(value="DESCRIZIONE MACRO AREA")
  private String macroAreaDescription;
  @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO", required = true)
  @JsonProperty(value="CODICE TIPOLOGIA SERVIZIO")
  private String serviceTypologyCode;
  @CsvBindByName(column = "TIPO SERVIZIO", required = true)
  @JsonProperty(value="TIPO SERVIZIO")
  private String serviceType;
  @CsvBindByName(column = "MOTIVO GIURIDICO DELLA RISCOSSIONE", required = true)
  @JsonProperty(value="MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String legalBasisOfCollection;
  @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO", required = true)
  @JsonProperty(value="DESCRIZIONE TIPO SERVIZIO")
  private String serviceTypeDescription;
  @CsvBindByName(column = "VERSIONE TASSONOMIA", required = true)
  @JsonProperty(value="VERSIONE TASSONOMIA")
  private String taxonomyVersion;
  @CsvBindByName(column = "DATI SPECIFICI DI INCASSO", required = true)
  @JsonProperty(value="DATI SPECIFICI INCASSO")
  private String specificCollectionData;
  @CsvBindByName(column = "DATA INIZIO VALIDITA", required = true)
  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String validityStartDate;
  @CsvBindByName(column = "DATA FINE VALIDITA", required = true)
  @JsonProperty(value="DATA FINE VALIDITA")
  private String validityEndDate;
  @CsvBindByName(column = "COMBINAZIONE TOPIC E SUBTOPIC", required = true)
  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String topicSubtopicCombination;
  @CsvBindByName(column = "FLAG NUOVA COMBINAZIONE", required = true)
  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String newCombinationFlag;
}
