package com.yapp.itemfinder.sample

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(basePackages = ["com.yapp.itemfinder.sample"])
class SampleExceptionHandler : ResponseEntityExceptionHandler() {

    // null 혹은 형식 오류
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("message=${ex.message}")
        val message = when (val exception = ex.cause) {
            is MissingKotlinParameterException -> "${exception.parameter.name.orEmpty()}는 널이어서는 안됩니다"
            is InvalidFormatException -> "${exception.path.last().fieldName.orEmpty()}는 올바른 형식이어야 합니다"
            else -> exception?.message?:"올바르지 않은 요청입니다."
        }
        return ResponseEntity.badRequest().body(ErrorMessage(message))
    }

    // validation 오류
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("message=${ex.message}")
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { fieldError -> fieldError.defaultMessage.orEmpty() }
        return ResponseEntity.badRequest().body(ErrorMessage(message))
    }

    // 기타 오류
    @ExceptionHandler(Exception::class)
    fun handleBadRequestException(ex: Exception): ResponseEntity<Any> {
        logger.error("message=${ex.message}")
        return ResponseEntity.badRequest().body(ErrorMessage(ex.message?:"올바르지 않은 요청입니다."))
    }
}

data class ErrorMessage(
    val message: String
)
