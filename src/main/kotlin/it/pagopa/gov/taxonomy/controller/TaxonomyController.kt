package it.pagopa.gov.taxonomy.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import it.pagopa.gov.taxonomy.enity.TaxonomyInfo
import it.pagopa.gov.taxonomy.model.ProblemJson
import it.pagopa.gov.taxonomy.service.TaxonomyService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/taxonomies")
@Tag(name = "Taxonomy")
class TaxonomyController(private val taxonomyService: TaxonomyService) {

    @Operation(
        summary = "To create a new taxonomy",
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Request created.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = TaxonomyInfo::class)
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Malformed request.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "Wrong or missing function key.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "404",
            description = "Not found.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Service unavailable.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        )]
    )
    @GetMapping
    fun getTaxonomies(@RequestParam("code") code: String?): List<TaxonomyInfo> {
        return taxonomyService.getTaxonomies(code);
    }

    @Operation(
        summary = "To create a new taxonomy",
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Request created.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = TaxonomyInfo::class)
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Malformed request.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "Wrong or missing function key.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "409",
            description = "Conflict.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Service unavailable.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        )]
    )
    @PostMapping
    fun createTaxonomyInfo(@RequestBody body: TaxonomyInfo): TaxonomyInfo {
        return taxonomyService.createTaxonomy(body);
    }

    @Operation(
        summary = "To delete a taxonomy from file",
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Request deleted.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema()
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Malformed request.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "Wrong or missing function key.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "404",
            description = "Not Found.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Service unavailable.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        )]
    )
    @DeleteMapping()
    fun deleteTaxonomy(@RequestParam("code", required = true) code: String) {
        taxonomyService.deleteTaxonomy(code);
    }


    @Operation(
        summary = "To create a new taxonomy from file",
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Request created.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = TaxonomyInfo::class)
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Malformed request.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "Wrong or missing function key.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "409",
            description = "Conflict.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Service unavailable.",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ProblemJson::class)
            )]
        )]
    )
    @PostMapping("/upload")
    fun createFromFile(@RequestParam("file") file: MultipartFile): List<TaxonomyInfo> {
        return taxonomyService.createFromFile(file);
    }


}
