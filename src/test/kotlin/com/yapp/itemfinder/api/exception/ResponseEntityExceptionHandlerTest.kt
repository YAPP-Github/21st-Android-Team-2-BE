package com.yapp.itemfinder.api.exception

import com.yapp.itemfinder.ControllerIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class ResponseEntityExceptionHandlerTest : ControllerIntegrationTest() {

    @Test
    fun `잘못된 Method 요청 시, 405 코드로 응답한다`() {
        mockMvc.post("/")
            .andExpect {
                status { isMethodNotAllowed() }
            }
    }

    @Test
    fun `존재하지 않는 URL 요청 시, 404 코드로 응답한다`() {
        mockMvc.get("/okay")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `올바른 요청에는 200 코드로 응답한다`() {
        mockMvc.get("/")
            .andExpect {
                status { isOk() }
            }
    }
}
