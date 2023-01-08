package com.yapp.itemfinder.domain.auth.service

import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.JwtTokenUtil.createToken
import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.api.exception.UnauthorizedException
import com.yapp.itemfinder.config.JwtTokenProvider
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
import com.yapp.itemfinder.domain.auth.dto.ReissueRequest
import com.yapp.itemfinder.domain.auth.dto.SignUpRequest
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.auth.repository.TokenRepository
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.token.TokenEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

class AuthServiceTest() : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>(relaxed = true)
    val tokenRepository = mockk<TokenRepository>()
    val tokenProvider = JwtTokenProvider("5dc5ef5de6e3094ec5fd308585eeff44950e9d8b87e95044bcbf7ec7200fd968632d73ee605c07df2a9d1f7dd6e5ced6903f9d029f682464079d311daeebb339")
    val authService = AuthService(memberRepository, tokenProvider, tokenRepository)

    Given("존재하는 회원의 경우") {
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        val member = MemberEntity(social = social, email = "test@email.com", name = "member")

        every { memberRepository.findBySocial(social) } returns member
        every { memberRepository.existsBySocial(social) } returns true
        every { tokenRepository.save(any()) } answers { firstArg() }

        When("소셜 로그인을 하면") {
            val loginRequest = LoginRequest(socialId, SocialType.KAKAO)
            val tokenResponse = authService.loginAndCreateTokens(loginRequest)

            Then("로그인에 성공하고, 토큰이 발급된다") {
                tokenProvider.getSubject(tokenResponse.accessToken) shouldBe member.id.toString()
                tokenProvider.getSubject(tokenResponse.refreshToken) shouldBe member.id.toString()
            }
        }

        When("회원가입을 하면") {
            val signUpRequest = SignUpRequest(socialId, SocialType.KAKAO, member.name)

            Then("회원가입에 실패한다") {
                shouldThrow<ConflictException> {
                    authService.createMemberAndLogin(signUpRequest)
                }
            }
        }
    }

    Given("존재하지 않는 회원의 경우") {
        val socialId = "123456789"

        every { memberRepository.existsBySocial(any()) } returns false
        every { memberRepository.save(any()) } answers { firstArg() }
        every { tokenRepository.save(any()) } answers { firstArg() }

        When("회원가입을 하면") {
            val signUpRequest = SignUpRequest(socialId, SocialType.KAKAO, "member")
            val tokens = authService.createMemberAndLogin(signUpRequest)

            Then("회원가입에 성공한다") {
                tokens shouldNotBe null
            }
        }
    }

    Given("유효한 리프레시 토큰이 주어진 경우") {
        val member = createFakeMemberEntity()

        val refreshToken = createToken(member.id.toString(), 1000 * 60)
        val reissueRequest = ReissueRequest(refreshToken)

        every { tokenRepository.findByIdOrNull(member.id) } returns TokenEntity(memberId = member.id, refreshToken = refreshToken)

        When("토큰을 재발급하면") {
            val reissueToken = authService.reissueToken(request = reissueRequest)

            Then("액세스 토큰 재발급에 성공한다") {
                reissueToken shouldNotBe null
            }
        }
    }

    Given("만료된 리프레시 토큰이 주어진 경우") {
        val member = createFakeMemberEntity()

        val refreshToken = createToken(member.id.toString(), 1)

        every { tokenRepository.findByIdOrNull(member.id) } returns null

        When("토큰을 재발급하면") {
            val reissueRequest = ReissueRequest(refreshToken)

            Then("액세스 토큰 재발급에 실패한다") {
                shouldThrow<UnauthorizedException> {
                    authService.reissueToken(request = reissueRequest)
                }
            }
        }
    }
})
