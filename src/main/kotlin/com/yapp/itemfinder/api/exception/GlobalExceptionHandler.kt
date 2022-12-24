package com.yapp.itemfinder.api.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private const val INTERNAL_SERVER_ERROR_MESSAGE = "알 수 없는 오류가 발생하였습니다."

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    // validation 오류
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("message=${ex.message}")
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { fieldError -> fieldError.defaultMessage.orEmpty() }
        return ResponseEntity.badRequest()
            .body(ErrorResponse(message = message))
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        logger.error("message=${ex.message}")
        return ResponseEntity.status(ex.httpStatus)
            .body(ErrorResponse(ex.message, ex.errorCode?.value))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("message=${ex.message}")
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(message = INTERNAL_SERVER_ERROR_MESSAGE))
    }

}
