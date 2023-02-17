package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.space.dto.SpaceResponse
import com.yapp.itemfinder.domain.space.dto.SpacesResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
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

        // when
        val mvcResult = mockMvc.post("/spaces") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateSpaceRequest(givenNonExistSpaceName))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val result = objectMapper.readValue(mvcResult.response.contentAsString, SpaceResponse::class.java)
        result.id shouldNotBe null
        result.name shouldBe givenNonExistSpaceName
    }

    @Test
    fun `회원이 공간을 삭제하면 공간 내부의 모든 보관함과 물건이 함께 삭제된다`() {
        // given
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = testMember))
        val givenContainers = mutableListOf<ContainerEntity>()
        repeat(3) {
            val container = containerRepository.save(createFakeContainerEntity(space = givenSpace))
            givenContainers.add(container)
            val firstItem = itemRepository.save(createFakeItemEntity(container = container))
            val secondItem = itemRepository.save(createFakeItemEntity(container = container))
            createItemTag(firstItem)
            createItemTag(secondItem)
        }

        // when
        mockMvc.delete("/spaces/${givenSpace.id}") {
        }.andExpect {
            status { isOk() }
        }

        // then
        spaceRepository.findById(givenSpace.id).isEmpty shouldBe true
        containerRepository.findBySpace(givenSpace).isEmpty() shouldBe true
        itemRepository.findAll().none { givenContainers.contains(it.container) } shouldBe true
    }
}
