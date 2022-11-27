package com.yapp.itemfinder.sample.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.itemfinder.sample.ErrorMessage
import com.yapp.itemfinder.sample.service.dto.CreateUserReq
import io.kotest.core.spec.style.AnnotationSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@SpringBootTest
@Transactional
class SampleUserControllerTest(
    private val objectMapper: ObjectMapper,
    private val webApplicationContext: WebApplicationContext
) : AnnotationSpec() {

    lateinit var mockMvc: MockMvc

    @BeforeEach
    internal fun setUp(
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }

    @Test
    fun `올바른 형식의 이메일이 아닌 경우 회원가입에 실패한다`() {
        mockMvc.post("/sample-user") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateUserReq("홍길동", "user1gmail.com"))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
            content { json(objectMapper.writeValueAsString(ErrorMessage("올바른 형식의 이메일이어야 합니다"))) }
        }
    }

    @Test
    fun `올바른 형식의 이름이 아닌 경우 회원가입에 실패한다`() {
        mockMvc.post("/sample-user") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateUserReq("user1", "user1@gmail.com"))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
            content { json(objectMapper.writeValueAsString(ErrorMessage("올바른 형식의 이름이어야 합니다"))) }
        }
    }

    @Test
    fun `회원가입에 성공한다`() {
        mockMvc.post("/sample-user") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateUserReq("홍길동", "user1@gmail.com"))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

}
