package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaxonomyObject {
  @CsvBindByName(column = "CODICE TIPO ENTE CREDITORE")
  @JsonProperty(value="CODICE TIPO ENTE CREDITORE")
  private String codiceTipoEnteCreditore;
  @CsvBindByName(column = "TIPO ENTE CREDITORE")
  @JsonProperty(value="TIPO ENTE CREDITORE")
  private String tipoEnteCreditore;
  @CsvBindByName(column = "PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  @JsonProperty(value="PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String progressivoMacroAreaPerEnteCreditore;
  @CsvBindByName(column = "NOME MACRO AREA")
  @JsonProperty(value="NOME MACRO AREA")
  private String nomeMacroArea;
  @CsvBindByName(column = "DESCRIZIONE MACRO AREA")
  @JsonProperty(value="DESCRIZIONE MACRO AREA")
  private String descrizioneMacroArea;
  @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO")
  @JsonProperty(value="CODICE TIPOLOGIA SERVIZIO")
  private String codiceTipologiaServizio;
  @CsvBindByName(column = "TIPO SERVIZIO")
  @JsonProperty(value="TIPO SERVIZIO")
  private String tipoServizio;
  @CsvBindByName(column = "MOTIVO GIURIDICO DELLA RISCOSSIONE")
  @JsonProperty(value="MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String motivoGiuridicoRiscossione;
  @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO")
  @JsonProperty(value="DESCRIZIONE TIPO SERVIZIO")
  private String descrizioneTipoServizio;
  @CsvBindByName(column = "VERSIONE TASSONOMIA")
  @JsonProperty(value="VERSIONE TASSONOMIA")
  private String versioneTassonomia;
  @CsvBindByName(column = "DATI SPECIFICI DI INCASSO")
  @JsonProperty(value="DATI SPECIFICI INCASSO")
  private String datiSpecificiIncasso;
  @CsvBindByName(column = "DATA INIZIO VALIDITA")
  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String dataInizioValidita;
  @CsvBindByName(column = "DATA FINE VALIDITA")
  @JsonProperty(value="DATA FINE VALIDITA")
  private String dataFineValidita;
  @CsvBindByName(column = "COMBINAZIONE TOPIC E SUBTOPIC")
  @JsonProperty(value="COMBINAZIONE TOPIC E SUBTOPIC")
  private String combinazioneTopicSubtopic;
  @CsvBindByName(column = "FLAG NUOVA COMBINAZIONE")
  @JsonProperty(value="FLAG NUOVA COMBINAZIONE")
  private String flagNuovaCombinazione;
}
