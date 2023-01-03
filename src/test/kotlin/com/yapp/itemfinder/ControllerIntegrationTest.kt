package com.yapp.itemfinder

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yapp.itemfinder.api.auth.LoginMemberArgumentResolver
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.MemberRepository
import com.yapp.itemfinder.domain.entity.space.SpaceRepository
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@SpringBootTest
@Transactional
abstract class ControllerIntegrationTest {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var spaceRepository: SpaceRepository

    @MockkBean
    lateinit var loginMemberArgumentResolver: LoginMemberArgumentResolver

    lateinit var testMember: MemberEntity
    @BeforeEach
    internal fun setUp(
        webApplicationContext: WebApplicationContext
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
        testMember = createTestMember()
    }
    protected fun createTestMember(): MemberEntity {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())

        every { loginMemberArgumentResolver.supportsParameter(any()) } returns true
        every { loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns givenMember

        return givenMember
    }
}
