package com.yapp.itemfinder.domain.auth.dto

import com.yapp.itemfinder.domain.member.SocialType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LoginRequest(
    @field:NotBlank(message = "소셜 아이디 값이 공백입니다")
    var socialId: String,
    @field:NotNull
    var socialType: SocialType
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
