package com.yapp.itemfinder.api

import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.api.exception.UnauthorizedException
import com.yapp.itemfinder.config.JwtTokenProvider
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.repository.MemberRepository
import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

const val BEARER = "Bearer"

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginMember()

@Component
class LoginMemberResolver(
    private val tokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginMember::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): MemberEntity {
        val token = extractBearerToken(webRequest)
        val memberId = tokenProvider.getSubject(token).toLong()
        return memberRepository.findByIdOrNull(memberId) ?: throw NotFoundException(message = "존재하지 않는 회원입니다.")
    }

    private fun extractBearerToken(webRequest: NativeWebRequest): String {
        val authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION) ?: throw UnauthorizedException(message = "토큰을 입력해주세요.")
        try {
            val (tokenType, token) = authorization.split(" ")
            if (tokenType != BEARER) {
                throw UnauthorizedException(message = "잘못된 토큰 타입입니다.")
            }
            return token
        } catch (e: IndexOutOfBoundsException) {
            throw UnauthorizedException()
        }
    }
}