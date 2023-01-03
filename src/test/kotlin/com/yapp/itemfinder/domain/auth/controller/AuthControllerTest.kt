package com.yapp.itemfinder.domain.auth.controller

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.member.repository.MemberRepository
import com.yapp.itemfinder.domain.auth.dto.LoginRequest
import com.yapp.itemfinder.domain.auth.dto.LoginResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class AuthControllerTest : ControllerIntegrationTest() {

    @Autowired
    lateinit var memberRepository: MemberRepository

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
    fun `만료기간이 지나지 않은 토큰으로 로그아웃할 수 있다`() {
        // given
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        memberRepository.save(MemberEntity(email = "test@email.com", name = "member", social = social))
        val result = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(social.socialId, social.socialType))
            accept = MediaType.APPLICATION_JSON
        }.andReturn()
        val tokenResponse = objectMapper.readValue(result.response.contentAsString, LoginResponse::class.java)

        // when
        mockMvc.get("/auth/logout") {
            header(name = HttpHeaders.AUTHORIZATION, values = arrayOf("Bearer " + tokenResponse.accessToken))
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `토큰이 없으면 로그아웃에 실패한다`() {
        // given
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        memberRepository.save(MemberEntity(email = "test@email.com", name = "member", social = social))
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(social.socialId, social.socialType))
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        // when
        mockMvc.get("/auth/logout")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `올바른 형식의 토큰이 아니면 로그아웃에 실패한다`() {
        // given
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        memberRepository.save(MemberEntity(email = "test@email.com", name = "member", social = social))
        val result = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(LoginRequest(social.socialId, social.socialType))
            accept = MediaType.APPLICATION_JSON
        }.andReturn()
        val tokenResponse = objectMapper.readValue(result.response.contentAsString, LoginResponse::class.java)

        // when
        mockMvc.get("/auth/logout") {
            header(name = HttpHeaders.AUTHORIZATION, values = arrayOf("Beerer " + tokenResponse.accessToken)) // Bearer이 아닌 Beerer
        }.andExpect {
            status { isUnauthorized() }
        }
    }
}
