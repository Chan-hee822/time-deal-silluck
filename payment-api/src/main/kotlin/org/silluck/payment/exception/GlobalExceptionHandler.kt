package org.silluck.payment.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.silluck.payment.exception.ErrorCode.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(PaymentException::class)
    fun handlePaymentException(
        e: PaymentException
    ): ErrorResponse {
        log.error(e) { "${e.errorCode} is occurred." }
        return ErrorResponse(e.errorCode)
    }

    @ExceptionHandler(PaymentException::class)
    fun handleException(
        e: Exception
    ): ErrorResponse {
        log.error(e) { "Exception is occurred." }
        return ErrorResponse(INTERNAL_SERVER_ERROR)
    }

}

class ErrorResponse(
    private val errorCode: ErrorCode,
    val errorMessage: String = errorCode.errorMessage,
)