package com.yapp.itemfinder.domain.auth.dto

import io.swagger.v3.oas.annotations.responses.ApiResponse

data class ReissueRequest(
    val accessToken: String,
    val refreshToken: String
)

@ApiResponse
data class ReissueResponse(
    val accessToken: String
)
