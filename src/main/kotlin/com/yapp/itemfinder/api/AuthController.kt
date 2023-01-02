package com.yapp.itemfinder.api

import com.yapp.itemfinder.api.exception.ErrorResponse
import com.yapp.itemfinder.domain.entity.member.Social
import com.yapp.itemfinder.domain.service.AuthService
import com.yapp.itemfinder.domain.service.dto.LoginRequest
import com.yapp.itemfinder.domain.service.dto.LoginResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = LoginResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 형식",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않은 회원",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun login(
        @RequestBody @Valid
        loginInfo: LoginRequest
    ): LoginResponse {
        return authService.loginAndCreateTokens(Social(loginInfo.socialType, loginInfo.socialId))
    }
}
