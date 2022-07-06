package it.pagopa.gov.taxonomy.exception

import lombok.EqualsAndHashCode
import lombok.Value
import org.springframework.http.HttpStatus
import javax.validation.constraints.NotNull


@EqualsAndHashCode(callSuper = true)
@Value
class AppException : RuntimeException {
    /**
     * title returned to the response when this exception occurred
     */
    var title: String

    /**
     * http status returned to the response when this exception occurred
     */
    var httpStatus: HttpStatus

    /**
     * @param httpStatus HTTP status returned to the response
     * @param title      title returned to the response when this exception occurred
     * @param message    the detail message returend to the response
     * @param cause      The cause of this [AppException]
     */
    constructor(
        @NotNull httpStatus: HttpStatus,
        @NotNull title: String,
        @NotNull message: String?,
        cause: Throwable?
    ) : super(message, cause) {
        this.title = title
        this.httpStatus = httpStatus
    }

    /**
     * @param httpStatus HTTP status returned to the response
     * @param title      title returned to the response when this exception occurred
     * @param message    the detail message returend to the response
     */
    constructor(@NotNull httpStatus: HttpStatus, @NotNull title: String, @NotNull message: String?) : super(message) {
        this.title = title
        this.httpStatus = httpStatus
    }

    /**
     * @param appError Response template returned to the response
     * @param args     [Formatter] replaces the placeholders in "details" string of [AppError] with the arguments.
     * If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    constructor(@NotNull appError: AppError, vararg args: Any?) : super(formatDetails(appError, *args)) {
        httpStatus = appError.httpStatus
        title = appError.title
    }

    /**
     * @param appError Response template returned to the response
     * @param cause    The cause of this [AppException]
     * @param args     Arguments for the details of [AppError] replaced by the [Formatter].
     * If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    constructor(@NotNull appError: AppError, cause: Throwable?, vararg args: Any?) : super(
        formatDetails(appError, *args),
        cause
    ) {
        httpStatus = appError.httpStatus
        title = appError.title
    }

    override fun toString(): String {
        return "AppException(" + httpStatus + ", " + title + ")" + super.toString()
    }

    companion object {
        private fun formatDetails(appError: AppError, vararg args: Any?): String {
            return String.format(appError.details, *args)
        }
    }
}
