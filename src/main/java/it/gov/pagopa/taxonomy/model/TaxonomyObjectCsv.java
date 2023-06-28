package it.gov.pagopa.taxonomy.model;

import com.opencsv.bean.CsvBindByName;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaxonomyObjectCsv {
  @CsvBindByName(column = "CODICE TIPO ENTE CREDITORE")
  private String codiceTipoEnteCreditore;
  @CsvBindByName(column = "TIPO ENTE CREDITORE")
  private String tipoEnteCreditore;
  @CsvBindByName(column = "PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String progressivoMacroAreaPerEnteCreditore;
  @CsvBindByName(column = "NOME MACRO AREA")
  private String nomeMacroArea;
  @CsvBindByName(column = "DESCRIZIONE MACRO AREA")
  private String descrizioneMacroArea;
  @CsvBindByName(column = "CODICE TIPOLOGIA SERVIZIO")
  private String codiceTipologiaServizio;
  @CsvBindByName(column = "TIPO SERVIZIO")
  private String tipoServizio;
  @CsvBindByName(column = "MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String motivoGiuridicoRiscossione;
  @CsvBindByName(column = "DESCRIZIONE TIPO SERVIZIO")
  private String descrizioneTipoServizio;
  @CsvBindByName(column = "VERSIONE TASSONOMIA")
  private String versioneTassonomia;
  @CsvBindByName(column = "DATI SPECIFICI DI INCASSO")
  private String datiSpecificiIncasso;
  @CsvBindByName(column = "DATA INIZIO VALIDITA")
  private String dataInizioValidita;
  @CsvBindByName(column = "DATA FINE VALIDITA")
  private String dataFineValidita;
  @CsvBindByName(column = "COMBINAZIONE TOPIC E SUBTOPIC")
  private String combinazioneTopicSubtopic;
  @CsvBindByName(column = "FLAG NUOVA COMBINAZIONE")
  private String flagNuovaCombinazione;
}