package com.yapp.itemfinder.domain.entity.space.controller

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.space.dto.SpacesResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class SpaceControllerTest : ControllerIntegrationTest() {

    @Test
    fun `요청한 멤버가 등록한 공간 정보들을 확인할 수 있다`() {
        // given
        val givenSpaceName = "공간 이름"
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = testMember, name = givenSpaceName))

        // when
        val mvcResult = mockMvc.get("/spaces")
            .andExpect { status { is2xxSuccessful() } }.andReturn()

        val result = objectMapper.readValue(mvcResult.response.contentAsString, SpacesResponse::class.java)

        // then
        result.spaces.size shouldBe 1

        with(result.spaces.first()) {
            id shouldBe givenSpace.id
            name shouldBe givenSpaceName
        }
    }

    @Test
    fun `멤버가 이미 등록한 공간명으로 공간 추가를 요청한 경우 공간 추가에 실패한다`() {
        // given
        val givenExistSpaceName = "이미 있는 공간명"
        spaceRepository.save(createFakeSpaceEntity(member = testMember, name = givenExistSpaceName))

        // when & expect
        mockMvc.post("/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateSpaceRequest(givenExistSpaceName))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `멤버는 기존에 등록하지 않은 새로운 공간명으로 공간을 추가할 수 있다`() {
        // given
        val givenNonExistSpaceName = "새로운 공간명"

        // when & expect
        mockMvc.post("/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateSpaceRequest(givenNonExistSpaceName))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}
