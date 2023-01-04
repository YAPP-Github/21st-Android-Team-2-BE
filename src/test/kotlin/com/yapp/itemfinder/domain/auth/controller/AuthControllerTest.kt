package com.yapp.itemfinder.domain.auth.controller

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
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
}
