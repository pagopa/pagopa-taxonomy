package it.gov.pagopa.taxonomy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaxonomyObjectStandard {
  @JsonProperty(value="CODICE TIPO ENTE CREDITORE")
  private String codiceTipoEnteCreditore;
  @JsonProperty(value="TIPO ENTE CREDITORE")
  private String tipoEnteCreditore;
  @JsonProperty(value="PROGRESSIVO MACRO AREA PER ENTE CREDITORE")
  private String progressivoMacroAreaPerEnteCreditore;
  @JsonProperty(value="NOME MACRO AREA")
  private String nomeMacroArea;
  @JsonProperty(value="DESCRIZIONE MACRO AREA")
  private String descrizioneMacroArea;
  @JsonProperty(value="CODICE TIPOLOGIA SERVIZIO")
  private String codiceTipologiaServizio;
  @JsonProperty(value="TIPO SERVIZIO")
  private String tipoServizio;
  @JsonProperty(value="MOTIVO GIURIDICO DELLA RISCOSSIONE")
  private String motivoGiuridicoRiscossione;
  @JsonProperty(value="DESCRIZIONE TIPO SERVIZIO")
  private String descrizioneTipoServizio;
  @JsonProperty(value="VERSIONE TASSONOMIA")
  private String versioneTassonomia;
  @JsonProperty(value="DATI SPECIFICI INCASSO")
  private String datiSpecificiIncasso;
  @JsonProperty(value="DATA INIZIO VALIDITA")
  private String dataInizioValidita;
  @JsonProperty(value="DATA FINE VALIDITA")
  private String dataFineValidita;
}