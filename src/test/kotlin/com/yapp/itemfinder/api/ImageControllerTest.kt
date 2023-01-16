package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.api.exception.ErrorResponse
import io.kotest.matchers.shouldBe
import org.apache.http.entity.ContentType.IMAGE_PNG
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_PDF_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.multipart

class ImageControllerTest : ControllerIntegrationTest() {

    @Test
    fun `회원은 이미지를 등록할 수 있다`() {
        // given
        val key = "images"
        val multipartFile = MockMultipartFile(key, "imageName.png", IMAGE_PNG.mimeType, "test".toByteArray())

        // when
        val result = mockMvc.multipart("/images") {
            file(multipartFile)
            contentType = MediaType.MULTIPART_FORM_DATA
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val urlList = objectMapper.readValue(result.response.contentAsString, List::class.java)
        urlList.size shouldBe 1
    }

    @Test
    fun `이미지가 아닌 파일은 등록할 수 없다`() {
        // given
        val key = "images"
        val multipartFile = MockMultipartFile(key, "fileName.pdf", APPLICATION_PDF_VALUE, "test".toByteArray())

        // when
        val result = mockMvc.multipart("/images") {
            file(multipartFile)
            contentType = MediaType.MULTIPART_FORM_DATA
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()

        // then
        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        errorResponse.message shouldBe INVALID_FILE_FORMAT
    }

    @Test
    fun `파일 개수가 10개를 초과하면 등록할 수 없다`() {
        // given
        val key = "images"
        val multipartFile = MockMultipartFile(key, "fileName.pdf", APPLICATION_PDF_VALUE, "test".toByteArray())

        // when & expect
        mockMvc.multipart("/images") {
            // 11개
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            file(multipartFile)
            contentType = MediaType.MULTIPART_FORM_DATA
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
