package com.yapp.itemfinder.domain.auth.dto

data class ReissueRequest(
    val refreshToken: String
)

data class ReissueResponse(
    val accessToken: String
)
