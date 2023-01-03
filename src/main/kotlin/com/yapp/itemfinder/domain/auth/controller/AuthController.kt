package com.yapp.itemfinder.domain.auth.controller

import com.yapp.itemfinder.api.LoginMember
import com.yapp.itemfinder.api.exception.ErrorResponse
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.auth.service.AuthService
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
import com.yapp.itemfinder.domain.auth.dto.LoginResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/auth")
@Tag(name = "인증")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    @Operation(summary = "소셜 로그인")
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
        return authService.loginAndCreateTokens(loginInfo)
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공"
            ),
            ApiResponse(
                responseCode = "401",
                description = "null이거나 유효하지 않은 토큰"
            )
        ]
    )
    fun logout(
        @Parameter(hidden = true)
        @LoginMember
        member: MemberEntity
    ) {
        authService.logout(member.id)
    }
}
