package com.yapp.itemfinder.api.exception

import org.springframework.http.HttpStatus

open class BaseException(
    val httpStatus: HttpStatus,
    message: String? = null,
    val errorCode: ErrorCode? = null
) : RuntimeException(message)

class NotFoundException(
    httpStatus: HttpStatus = HttpStatus.NOT_FOUND,
    message: String? = null,
    errorCode: ErrorCode? = null
) : BaseException(httpStatus, message, errorCode)

class BadRequestException(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    message: String? = null,
    errorCode: ErrorCode? = null
) : BaseException(httpStatus, message, errorCode)

class ConflictException(
    httpStatus: HttpStatus = HttpStatus.CONFLICT,
    message: String? = null,
    errorCode: ErrorCode? = null
) : BaseException(httpStatus, message, errorCode)

class UnauthorizedException(
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    message: String? = null,
    errorCode: ErrorCode? = null
) : BaseException(httpStatus, message, errorCode)

class ForbiddenException(
    httpStatus: HttpStatus = HttpStatus.FORBIDDEN,
    message: String? = null,
    errorCode: ErrorCode? = null
) : BaseException(httpStatus, message, errorCode)

const val INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다."
