package com.yapp.itemfinder.api.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ConstraintViolationException

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
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { fieldError ->
            if (fieldError.defaultMessage == null) {
                "${fieldError.field} 의 형식이 올바르지 않습니다"
            } else {
                "${fieldError.field} 는 ${fieldError.defaultMessage}"
            }
        }
        return ResponseEntity.badRequest()
            .body(ErrorResponse(message = message))
    }

    // validation 오류
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val message = ex.constraintViolations.joinToString(", ") { "${it.propertyPath.last()} 는 ${it.message}" }
        return ResponseEntity.badRequest().body(ErrorResponse(message = message))
    }

    // null 혹은 형식 오류
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("message=${ex.message}")
        val message = when (val exception = ex.cause) {
            is MissingKotlinParameterException -> "${exception.parameter.name.orEmpty()}는 널이어서는 안됩니다."
            is InvalidFormatException -> "${exception.path.last().fieldName.orEmpty()}의 형식이 올바르지 않습니다."
            else -> exception?.message ?: "올바르지 않은 요청입니다."
        }
        return ResponseEntity.badRequest().body(ErrorResponse(message))
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        logger.error("message=${ex.message}")
        return ResponseEntity.status(ex.httpStatus)
            .body(ErrorResponse(ex.message, ex.errorCode?.value))
    }

    @ExceptionHandler(Exception::class, InternalServerException::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("message=${ex.message}")
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(message = INTERNAL_SERVER_ERROR_MESSAGE))
    }
}
