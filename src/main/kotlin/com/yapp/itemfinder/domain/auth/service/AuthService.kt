package com.yapp.itemfinder.domain.auth.service

import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.config.JwtTokenProvider
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.token.TokenEntity
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.auth.repository.TokenRepository
import com.yapp.itemfinder.domain.auth.dto.LoginResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberRepository: MemberRepository,
    private val tokenProvider: JwtTokenProvider,
    private val tokenRepository: TokenRepository
) {
    @Transactional
    fun loginAndCreateTokens(request: LoginRequest): LoginResponse {
        val memberId = getMemberIdBySocial(Social(socialId = request.socialId, socialType = request.socialType))
        val accessToken = tokenProvider.createAccessToken(memberId.toString())
        val refreshToken = tokenProvider.createRefreshToken(memberId.toString())
        tokenRepository.save(
            TokenEntity(
                memberId = memberId,
                refreshToken = refreshToken,
                timeToLive = tokenProvider.refreshTokenExpirationMilliseconds
            )
        )
        return LoginResponse(accessToken, refreshToken)
    }

    private fun getMemberIdBySocial(social: Social): Long {
        return memberRepository.findBySocial(social)?.id
            ?: throw NotFoundException(message = "존재하지 않는 회원입니다.")
    }

    @Transactional
    fun logout(memberId: Long) {
        tokenRepository.deleteById(memberId)
    }
}