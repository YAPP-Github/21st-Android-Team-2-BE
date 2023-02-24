package com.yapp.itemfinder.api

import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.common.Const.BEARER
import com.yapp.itemfinder.config.JwtTokenProvider
import com.yapp.itemfinder.domain.member.MemberRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@SpringBootTest
class ValidateMemberTest {
    @Autowired
    lateinit var tokenProvider: JwtTokenProvider
    @Autowired
    lateinit var memberRepository: MemberRepository
    lateinit var mockMvc: MockMvc

    @BeforeEach
    internal fun setUp(
        webApplicationContext: WebApplicationContext
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }

    @Test
    fun `유효한 토큰인 경우 200번 상태 코드를 반환한다`() {
        // given
        val member = memberRepository.save(createFakeMemberEntity())
        val token = tokenProvider.createAccessToken(member.id.toString())

        // when & expect
        mockMvc.get("/auth/validate-member") {
            header(HttpHeaders.AUTHORIZATION, "$BEARER $token")
        }.andExpect {
            status { isOk() }
        }
    }
}
