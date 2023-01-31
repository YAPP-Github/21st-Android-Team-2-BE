package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemDetailResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class ItemControllerTest : ControllerIntegrationTest() {

    @Test
    fun `회원이 등록한 보관함 안에 물건을 등록할 수 있다`() {
        // given
        val givenSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = testMember))
        val givenContainer = containerRepository.save(FakeEntity.createFakeContainerEntity(space = givenSpace))
        val request = CreateItemRequest(containerId = givenContainer.id, name = "물건 이름", itemType = ItemType.LIFE.name, quantity = 1)

        // when
        val result = mockMvc.post("/items") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val itemResponse = objectMapper.readValue(result.response.contentAsString, ItemDetailResponse::class.java)
        with(itemResponse) {
            name shouldBe request.name
            itemType shouldBe ItemType.LIFE.name
            quantity shouldBe request.quantity
            containerName shouldBe givenContainer.name
            containerImageUrl shouldBe givenContainer.imageUrl
            spaceName shouldBe givenSpace.name
        }
    }

    @Test
    fun `회원이 등록한 물건을 상세 조회할 수 있다`() {
        // given
        val givenItem = createItem(testMember)

        // when
        val result = mockMvc.get("/items/${givenItem.id}")
            .andExpect {
                status { isOk() }
            }.andReturn()

        // then
        val itemResponse = objectMapper.readValue(result.response.contentAsString, ItemDetailResponse::class.java)
        with(itemResponse) {
            id shouldBe givenItem.id
            name shouldBe givenItem.name
        }
    }

    @Test
    fun `존재하지 않은 물건은 조회할 수 없다`() {
        // given
        val itemId = TestUtil.generateRandomPositiveLongValue()

        // when & expect
        mockMvc.get("/items/$itemId")
            .andExpect {
                status { isNotFound() }
            }.andReturn()
    }

    @Test
    fun `다른 회원의 물건은 조회할 수 없다`() {
        // given
        val otherMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenItem = createItem(otherMember)

        // when & expect
        mockMvc.get("/items/${givenItem.id}")
            .andExpect {
                status { isForbidden() }
            }.andReturn()
    }
}
