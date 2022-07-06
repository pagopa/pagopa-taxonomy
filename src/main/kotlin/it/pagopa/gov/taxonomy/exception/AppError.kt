package it.pagopa.gov.taxonomy.exception

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class AppError(val httpStatus: HttpStatus, val title: String, val details: String) {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "Taxonomy %s not found"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict", "Taxonomy %s already exists"),
    UNKNOWN(HttpStatus.INSUFFICIENT_STORAGE, "", "");
}
