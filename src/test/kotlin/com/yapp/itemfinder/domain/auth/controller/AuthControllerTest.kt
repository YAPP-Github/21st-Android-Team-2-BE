package com.yapp.itemfinder.domain.auth.controller

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
import com.yapp.itemfinder.domain.auth.dto.SignUpRequest
import com.yapp.itemfinder.domain.member.MemberEntity
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthControllerTest : ControllerIntegrationTest() {

    @Test
    fun `존재하는 회원은 로그인할 수 있다`() {
        // given
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        memberRepository.save(MemberEntity(email = "test@email.com", name = "member", social = social))

        // when
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(social.socialId, social.socialType))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `존재하지 않는 회원은 로그인에 실패한다`() {
        // given
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)

        // when
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(social.socialId, social.socialType))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `올바르지 않은 형식이면 회원가입에 실패한다`() {
        // given
        val socialId = "123456789"
        val nickname = "member"
        val wrongEmail = "email"

        // when
        mockMvc.post("/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(SignUpRequest(socialId, SocialType.KAKAO, nickname = nickname, email = wrongEmail))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `처음으로 회원가입한 회원은 회원가입할 수 있다`() {
        // given
        val socialId = "123456789"
        val socialType = SocialType.KAKAO
        val nickname = "member"

        // when
        mockMvc.post("/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(SignUpRequest(socialId, socialType, nickname = nickname))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
