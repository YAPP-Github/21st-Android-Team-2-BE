package com.yapp.itemfinder.domain.auth.service

import com.yapp.itemfinder.config.JwtTokenProvider
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.member.repository.MemberRepository
import com.yapp.itemfinder.domain.auth.repository.TokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class AuthServiceTest() : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>()
    val tokenRepository = mockk<TokenRepository>()
    val tokenProvider = JwtTokenProvider("5dc5ef5de6e3094ec5fd308585eeff44950e9d8b87e95044bcbf7ec7200fd968632d73ee605c07df2a9d1f7dd6e5ced6903f9d029f682464079d311daeebb339")
    val authService = AuthService(memberRepository, tokenProvider, tokenRepository)

    Given("존재하는 회원의 경우") {
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        val member = MemberEntity(id = 1L, social = social, email = "", name = "")
        every { memberRepository.findBySocial(social) } returns member
        every { tokenRepository.save(any()) } answers { firstArg() }

        When("소셜 로그인을 하면") {
            val tokenResponse = authService.loginAndCreateTokens(social)

            Then("로그인에 성공하고, 토큰이 발급된다") {
                tokenProvider.getSubject(tokenResponse.accessToken) shouldBe member.id.toString()
                tokenProvider.getSubject(tokenResponse.refreshToken) shouldBe member.id.toString()
            }
        }
    }
})
