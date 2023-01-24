package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class ItemControllerTest : ControllerIntegrationTest() {

    @Test
    fun `회원이 등록한 보관함 안에 물건을 등록할 수 있다`() {
        // given
        val givenSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = testMember))
        val givenContainer = containerRepository.save(FakeEntity.createFakeContainerEntity(space = givenSpace))
        val request = CreateItemRequest(containerId = givenContainer.id, name = "물건 이름", category = ItemType.LIFESTYLE.name, quantity = 1)

        // when
        val result = mockMvc.post("/items") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val itemResponse = objectMapper.readValue(result.response.contentAsString, ItemResponse::class.java)
        with(itemResponse) {
            name shouldBe request.name
            category shouldBe ItemType.LIFESTYLE.value
            quantity shouldBe request.quantity
            containerName shouldBe givenContainer.name
            containerImageUrl shouldBe givenContainer.imageUrl
            spaceName shouldBe givenSpace.name
        }
    }
}
