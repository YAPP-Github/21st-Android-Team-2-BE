package com.yapp.itemfinder.api

import com.yapp.itemfinder.ControllerIntegrationTest
import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.domain.item.ItemEntity
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.delete
import javax.persistence.EntityManager

class ContainerControllerTest : ControllerIntegrationTest() {
    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `보관함 2개가 등록된 공간 내부의 보관함을 1개 삭제하면 내부 물건도 함께 삭제된다`() {
        // given
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = testMember))
        val givenContainer = containerRepository.save(createFakeContainerEntity(space = givenSpace))
        containerRepository.save(createFakeContainerEntity(space = givenSpace))
        val givenItems = mutableListOf<ItemEntity>()
        repeat(3) {
            givenItems.add(itemRepository.save(createFakeItemEntity(container = givenContainer)))
        }

        // when
        mockMvc.delete("/containers/${givenContainer.id}")
            .andExpect {
                status { isOk() }
            }

        // then
        entityManager.clear()
        containerRepository.findById(givenContainer.id).isEmpty shouldBe true
        givenItems.map { itemRepository.findById(it.id).isEmpty shouldBe true }
    }
}
