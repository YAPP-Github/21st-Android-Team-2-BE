package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemDetailResponse
import com.yapp.itemfinder.domain.item.dto.UpdateItemRequest
import com.yapp.itemfinder.domain.tag.ItemTagEntity
import com.yapp.itemfinder.domain.tag.TagEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun `회원이 등록한 물건을 삭제하면, 물건과 물건 태그가 함께 삭제된다`() {
        // given
        val givenItem = createItem(testMember)
        val givenTag = tagRepository.save(TagEntity(member = testMember, name = TestUtil.generateRandomString(4)))
        itemTagRepository.save(ItemTagEntity(item = givenItem, tag = givenTag))

        // when
        mockMvc.delete("/items/${givenItem.id}")
            .andExpect {
                status { isOk() }
            }

        // then
        itemRepository.findById(givenItem.id).isEmpty shouldBe true
        itemTagRepository.findItemIdAndTagNameByItemIdIsIn(listOf(givenItem.id)).isEmpty() shouldBe true
        tagRepository.findById(givenTag.id) shouldNotBe null
    }

    @Test
    fun `존재하지 않는 물건은 삭제할 수 없다`() {
        // given
        val itemId = TestUtil.generateRandomPositiveLongValue()

        // when & expect
        mockMvc.delete("/items/$itemId")
            .andExpect {
                status { isNotFound() }
            }.andReturn()
    }

    @Test
    fun `다른 회원의 물건은 삭제할 수 없다`() {
        // given
        val otherMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenItem = createItem(otherMember)

        // when & expect
        mockMvc.delete("/items/${givenItem.id}")
            .andExpect {
                status { isForbidden() }
            }.andReturn()
    }

    @Test
    fun `회원이 등록한 물건을 수정할 수 있다`() {
        // given
        val givenItem = createItem(testMember)
        val givenTag = tagRepository.save(TagEntity(member = testMember, name = TestUtil.generateRandomString(4)))
        itemTagRepository.save(ItemTagEntity(item = givenItem, tag = givenTag))
        val newName = "새로운 물건 이름"
        val request = UpdateItemRequest(containerId = givenItem.container.id, name = newName, itemType = givenItem.type.name, quantity = givenItem.quantity)

        // when
        val result = mockMvc.put("/items/${givenItem.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val itemResponse = objectMapper.readValue(result.response.contentAsString, ItemDetailResponse::class.java)
        itemResponse.name shouldBe newName
    }
}
