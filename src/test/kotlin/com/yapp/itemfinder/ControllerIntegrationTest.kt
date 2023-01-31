package com.yapp.itemfinder

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yapp.itemfinder.api.LoginMemberResolver
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.ItemEntity
import com.yapp.itemfinder.domain.item.ItemRepository
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
@Import(EmbeddedRedisConfig::class, AmazonS3TestConfig::class)
abstract class ControllerIntegrationTest {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var spaceRepository: SpaceRepository

    @Autowired
    lateinit var containerRepository: ContainerRepository

    @Autowired
    lateinit var itemRepository: ItemRepository

    @MockkBean
    lateinit var loginMemberArgumentResolver: LoginMemberResolver

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

    fun createItem(member: MemberEntity): ItemEntity {
        val givenSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = member))
        val givenContainer = containerRepository.save(FakeEntity.createFakeContainerEntity(space = givenSpace))
        return itemRepository.save(FakeEntity.createFakeItemEntity(container = givenContainer))
    }
}
