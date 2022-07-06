package it.pagopa.gov.taxonomy.exception

import it.pagopa.gov.taxonomy.model.ProblemJson
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ConstraintViolationException

/**
 * All Exceptions are handled by this class
 */
@ControllerAdvice
@Slf4j
class ErrorHandler : ResponseEntityExceptionHandler() {
    val log: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    /**
     * Handle if the input request is not a valid JSON
     *
     * @param ex      [HttpMessageNotReadableException] exception raised
     * @param headers of the response
     * @param status  of the response
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with a 400 as HTTP status
     */
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.warn("Input not readable: ", ex)
        val errorResponse = ProblemJson(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST,
            "Invalid input format"
        )
        return ResponseEntity<Any>(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handle if missing some request parameters in the request
     *
     * @param ex      [MissingServletRequestParameterException] exception raised
     * @param headers of the response
     * @param status  of the response
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with a 400 as HTTP status
     */
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.warn("Missing request parameter: ", ex)
        val errorResponse = ProblemJson(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST,
            ex.message
        )
        return ResponseEntity<Any>(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Customize the response for TypeMismatchException.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a `ResponseEntity` instance
     */
    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.warn("Type mismatch: ", ex)
        val errorResponse = ProblemJson(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST,

            String.format(
                "Invalid value %s for property %s",
                ex.value,
                (ex as MethodArgumentTypeMismatchException).getName()
            )
        )
        return ResponseEntity<Any>(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handle if validation constraints are unsatisfied
     *
     * @param ex      [MethodArgumentNotValidException] exception raised
     * @param headers of the response
     * @param status  of the response
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with a 400 as HTTP status
     */
    protected override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val details: MutableList<String> = ArrayList()
        for (error in ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage())
        }
        val detailsMessage = java.lang.String.join(", ", details)
        log.warn("Input not valid: $detailsMessage")
        val errorResponse = ProblemJson(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST,
            detailsMessage
        )
        return ResponseEntity<Any>(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * @param ex      [DataIntegrityViolationException] exception raised when the SQL statement cannot be executed
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with an appropriated HTTP status
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException,
        request: WebRequest?
    ): ResponseEntity<ProblemJson> {
        var errorResponse: ProblemJson? = null
        if (ex.cause is ConstraintViolationException) {
            log.warn("Can't delete from Database", ex)
            errorResponse = ProblemJson(
                HttpStatus.CONFLICT.value(),
                "Conflict with the current state of the resource",
                "There is a relation with other resource. Delete it first."
            )
        }

        // default response
        if (errorResponse == null) {
            log.warn("Data Integrity Violation", ex)
            errorResponse = ProblemJson(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR,
                ex.message ?: ""
            )
        }
        return ResponseEntity<ProblemJson>(errorResponse, HttpStatus.valueOf(errorResponse.status))
    }

    /**
     * Handle if a [AppException] is raised
     *
     * @param ex      [AppException] exception raised
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with an appropriated HTTP status
     */
    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException, request: WebRequest?): ResponseEntity<ProblemJson> {
        if (ex.cause != null) {
            log.warn(
                """
    App Exception raised: ${ex.message}
    Cause of the App Exception: 
    """.trimIndent(), ex.cause
            )
            log.trace("Trace error: ", ex)
        } else {
            log.warn("App Exception raised: ", ex)
        }
        val errorResponse = ProblemJson(
            ex.httpStatus.value(),
            ex.title,
            ex.message ?: ""
        )
        return ResponseEntity<ProblemJson>(errorResponse, ex.httpStatus)
    }

    /**
     * Handle if a [Exception] is raised
     *
     * @param ex      [Exception] exception raised
     * @param request from frontend
     * @return a [ProblemJson] as response with the cause and with 500 as HTTP status
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest?): ResponseEntity<ProblemJson> {
        log.error("Generic Exception raised:", ex)
        val errorResponse = ProblemJson(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            INTERNAL_SERVER_ERROR,
            "Unexpected exception raised"
        )
        return ResponseEntity<ProblemJson>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        const val INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR"
        const val BAD_REQUEST = "BAD REQUEST"
        const val FOREIGN_KEY_VIOLATION = "23503"
        const val CHILD_RECORD_VIOLATION = 2292
    }
}
