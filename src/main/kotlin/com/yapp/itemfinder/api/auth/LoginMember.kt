package com.yapp.itemfinder.api.auth

import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.MemberRepository
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.lang.RuntimeException

// TODO 해당 파일에 있는 내용은 모두 임시용입니다.
@Hidden
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class LoginMember

@Component
class LoginMemberArgumentResolver(
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
        // TODO (구현 전에 테스트 위해 샘플 데이터로 모킹한 상황)
        return memberRepository.findByIdOrNull(1L) ?: throw RuntimeException("회원이 없습니다.")
    }
}
