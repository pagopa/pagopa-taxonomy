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
  private String codiceTipoEnteCreditore;
  @CsvBindByName(column = "TIPO ENTE CREDITORE", required = true)
  @JsonProperty(value="TIPO ENTE CREDITORE")
  private String tipoEnteCreditore;
  @CsvBindByName(column = "PROGRESSIVO MACRO AREA PER ENTE CREDITORE", required = true)
  @JsonProperty(value="PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String progressivoMacroAreaPerEnteCreditore;
  @CsvBindByName(column = "NOME MACRO AREA", required = true)
  @JsonProperty(value="NOME MACRO AREA")
  private String nomeMacroArea;
  @CsvBindByName(column = "DESCRIZIONE MACRO AREA", required = true)
  @JsonProperty(value="DESCRIZIONE MACRO AREA")
  private String descrizioneMacroArea;
  @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO", required = true)
  @JsonProperty(value="CODICE TIPOLOGIA SERVIZIO")
  private String codiceTipologiaServizio;
  @CsvBindByName(column = "TIPO SERVIZIO", required = true)
  @JsonProperty(value="TIPO SERVIZIO")
  private String tipoServizio;
  @CsvBindByName(column = "MOTIVO GIURIDICO DELLA RISCOSSIONE", required = true)
  @JsonProperty(value="MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String motivoGiuridicoRiscossione;
  @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO", required = true)
  @JsonProperty(value="DESCRIZIONE TIPO SERVIZIO")
  private String descrizioneTipoServizio;
  @CsvBindByName(column = "VERSIONE TASSONOMIA", required = true)
  @JsonProperty(value="VERSIONE TASSONOMIA")
  private String versioneTassonomia;
  @CsvBindByName(column = "DATI SPECIFICI DI INCASSO", required = true)
  @JsonProperty(value="DATI SPECIFICI INCASSO")
  private String datiSpecificiIncasso;
  @CsvBindByName(column = "DATA INIZIO VALIDITA", required = true)
  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String dataInizioValidita;
  @CsvBindByName(column = "DATA FINE VALIDITA", required = true)
  @JsonProperty(value="DATA FINE VALIDITA")
  private String dataFineValidita;
  @CsvBindByName(column = "COMBINAZIONE TOPIC E SUBTOPIC", required = true)
  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String combinazioneTopicSubtopic;
  @CsvBindByName(column = "FLAG NUOVA COMBINAZIONE", required = true)
  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String flagNuovaCombinazione;
}
