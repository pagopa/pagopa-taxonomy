package it.gov.pagopa.taxonomy.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Taxonomy {

  @JsonProperty(value="CODICE TIPO ENTE CREDITORE")
  private String creditorEntityTypeCode;

  @JsonProperty(value="TIPO ENTE CREDITORE")
  private String creditorEntityType;

  @JsonProperty(value="PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String creditorEntityMacroAreaProgressive;

  @JsonProperty(value="NOME MACRO AREA")
  private String macroAreaName;

  @JsonProperty(value="DESCRIZIONE MACRO AREA")
  private String macroAreaDescription;

  @JsonProperty(value="CODICE TIPOLOGIA SERVIZIO")
  private String serviceTypologyCode;

  @JsonProperty(value="TIPO SERVIZIO")
  private String serviceType;

  @JsonProperty(value="MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String legalBasisOfCollection;

  @JsonProperty(value="DESCRIZIONE TIPO SERVIZIO")
  private String serviceTypeDescription;

  @JsonProperty(value="VERSIONE TASSONOMIA")
  private String taxonomyVersion;

  @JsonProperty(value="DATI SPECIFICI INCASSO")
  private String specificCollectionData;

  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String validityStartDate;

  @JsonProperty(value="DATA FINE VALIDITA")
  private String validityEndDate;

  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String topicSubtopicCombination;

  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String newCombinationFlag;
}
