package com.yapp.itemfinder.api.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val message: String? = null,
    val code: String? = null
)

enum class ErrorCode(val value: String)
