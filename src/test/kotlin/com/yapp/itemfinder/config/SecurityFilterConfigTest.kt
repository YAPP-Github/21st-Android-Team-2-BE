package com.yapp.itemfinder.config

import com.yapp.itemfinder.ControllerIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get

class SecurityFilterConfigTest : ControllerIntegrationTest() {
    @Value("\${springdoc.api-docs.path}")
    lateinit var apiDocPath: String

    @WithMockUser(value = "loginUser", authorities = ["USER"])
    @Test
    fun `일반 유저는 스웨거 페이지에 접속할 수 없다`() {
        // expect & then
        mockMvc.get(apiDocPath) {
        }.andExpect {
            status { is4xxClientError() }
        }.andReturn()
    }

    @WithMockUser(value = "admin", authorities = ["ADMIN"])
    @Test
    fun `어드민 권한을 가진 유저는 스웨거 페이지에 접속할 수 있다`() {
        // expect & then
        mockMvc.get(apiDocPath) {
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
    }
}
