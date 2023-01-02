package com.yapp.itemfinder.domain.service.dto

import com.yapp.itemfinder.domain.entity.member.SocialType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LoginRequest(
    @field:NotBlank(message = "socialId는 공백이어서는 안됩니다.")
    var socialId: String,
    @field:NotNull
    var socialType: SocialType
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
