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
    @CsvBindByName(column = "codice_tipo_ente_creditore", required = true)
    private String creditorEntityTypeCode;
    @CsvBindByName(column = "tipo_ente_creditore", required = true)
    private String creditorEntityType;
    @CsvBindByName(column = "progressivo_macro_area_per_ente_creditore", required = true)
    private String creditorEntityMacroAreaProgressive;
    @CsvBindByName(column = "nome_macro_area", required = true)
    private String macroAreaName;
    @CsvBindByName(column = "descrizione_macro_area", required = true)
    private String macroAreaDescription;
    @CsvBindByName(column = "codice_tipologia_servizio", required = true)
    private String serviceTypologyCode;
    @CsvBindByName(column = "tipo_servizio", required = true)
    private String serviceType;
    @CsvBindByName(column = "motivo_giuridico_della_riscossione", required = true)
    private String legalBasisOfCollection;
    @CsvBindByName(column = "descrizione_tipo_servizio", required = true)
    private String serviceTypeDescription;
    @CsvBindByName(column = "versione_tassonomia", required = true)
    private String taxonomyVersion;
    @CsvBindByName(column = "dati_specifici_di_incasso", required = true)
    private String specificCollectionData;
    @CsvBindByName(column = "data_inizio_validita", required = true)
    private String validityStartDate;
    @CsvBindByName(column = "data_fine_validita", required = true)
    private String validityEndDate;
    @CsvBindByName(column = "combinazione_topic_subtopic", required = true)
    private String topicSubtopicCombination;
}
