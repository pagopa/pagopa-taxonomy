package it.gov.pagopa.microservice.model;

import com.opencsv.bean.CsvBindByPosition;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaxonomyObjectCsv {
  @CsvBindByPosition(position = 0)
  private String codiceTipoEnteCreditore;
  @CsvBindByPosition(position = 1)
  private String tipoEnteCreditore;
  @CsvBindByPosition(position = 2)
  private String progressivoMacroAreaPerEnteCreditore;
  @CsvBindByPosition(position = 3)
  private String nomeMacroArea;
  @CsvBindByPosition(position = 4)
  private String descrizioneMacroArea;
  @CsvBindByPosition(position = 5)
  private String codiceTipologiaServizio;
  @CsvBindByPosition(position = 6)
  private String tipoServizio;
  @CsvBindByPosition(position = 7)
  private String motivoGiuridicoRiscossione;
  @CsvBindByPosition(position = 8)
  private String descrizioneTipoServizio;
  @CsvBindByPosition(position = 9)
  private String versioneTassonomia;
  @CsvBindByPosition(position = 10)
  private String datiSpecificiIncasso;
  @CsvBindByPosition(position = 11)
  private String dataInizioValidita;
  @CsvBindByPosition(position = 12)
  private String dataFineValidita;
  @CsvBindByPosition(position = 13)
  private String combinazioneTopicSubtopic;
  @CsvBindByPosition(position = 14)
  private String flagNuovaCombinazione;
}
