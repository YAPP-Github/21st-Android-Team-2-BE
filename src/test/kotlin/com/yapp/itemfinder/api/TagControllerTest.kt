package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.domain.tag.dto.CreateTagRequest
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class TagControllerTest : ControllerIntegrationTest() {
    @Test
    fun `회원은 10글자 이내의 태그를 등록할 수 있다`() {
        // given
        val request = CreateTagsRequest(listOf(CreateTagRequest(name = generateRandomString(10))))

        // when
        val result = mockMvc.post("/tags") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val tagResponse = objectMapper.readValue(result.response.contentAsString, TagsResponse::class.java)
        tagResponse.tags.size shouldBe 1
        tagResponse.tags[0].name shouldBe request.tags[0].name
    }

    @Test
    fun `회원은 10글자를 초과하는 태그를 등록할 수 없다`() {
        // given
        val request = CreateTagsRequest(listOf(CreateTagRequest(name = generateRandomString(11))))

        // when & expect
        mockMvc.post("/tags") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
