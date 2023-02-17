package com.yapp.itemfinder.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeResponse
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class TagControllerTest : ControllerIntegrationTest() {
    @Test
    fun `회원은 10글자 이내의 태그를 등록할 수 있다`() {
        // given
        val request = CreateTagsRequest(listOf(generateRandomString(10)))
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
        tagResponse.tags[0].name shouldBe request.tags[0]
    }

    @Test
    fun `회원의 전체 태그 목록을 조회할 수 있다`() {
        // given
        val tagCnt = 3
        repeat(tagCnt) {
            tagRepository.save(FakeEntity.createFakeTagEntity(member = testMember))
        }

        // when
        val result = mockMvc.get("/tags")
            .andExpect {
                status { isOk() }
            }.andReturn()

        // then
        val tagResponse = objectMapper.readValue(result.response.contentAsString, TagsResponse::class.java)
        tagResponse.tags.size shouldBe tagCnt
    }

    @Test
    fun `회원의 태그를 상세 조회할 수 있다`() {
        // given
        val tagCnt = 3
        repeat(tagCnt) {
            tagRepository.save(FakeEntity.createFakeTagEntity(member = testMember))
        }

        // when
        val page = 0
        val size = 10
        val result = mockMvc.get("/tags/detail") {
            param("page", page.toString())
            param("size", size.toString())
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val tagResponse = objectMapper.readValue<PageResponse<TagWithItemTypeResponse>>(result.response.contentAsString)
        tagResponse.data.size shouldBe tagCnt
        for (tag in tagResponse.data) {
            tag.itemType.size shouldBe ItemType.values().size
            tag.itemType.map { typeCount -> typeCount.count shouldBe 0 }
        }
    }

    @Test
    fun `태그 상세 조회 시 page가 0 미만이면 예외가 발생한다`() {
        // given
        val page = -1

        // when & expect
        mockMvc.get("/tags/detail") {
            param("page", page.toString())
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `태그 상세 조회 시 size가 10 초과이면 예외가 발생한다`() {
        // given
        val size = 11

        // when & expect
        mockMvc.get("/tags/detail") {
            param("size", size.toString())
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
