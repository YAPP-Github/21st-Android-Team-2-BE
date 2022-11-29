package com.yapp.itemfinder.sample.controller

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.sample.ErrorMessage
import com.yapp.itemfinder.sample.service.dto.CreateUserReq
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class SampleUserControllerTest : ControllerIntegrationTest() {

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
