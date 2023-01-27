package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.tag.ItemTagService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ItemServiceTest : BehaviorSpec({
    val itemRepository = mockk<ItemRepository>()
    val containerRepository = mockk<ContainerRepository>()
    val itemTagService = mockk<ItemTagService>()
    val itemService = ItemService(itemRepository, containerRepository, itemTagService)

    Given("이미지가 없는 보관함이 등록된 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenSpace = FakeEntity.createFakeSpaceEntity(member = givenMember)
        val givenContainer = FakeEntity.createFakeContainerEntity(space = givenSpace, imageUrl = null)

        every { containerRepository.findWithSpaceByIdAndMemberId(givenContainer.id, givenMember.id) } returns givenContainer
        every { itemRepository.save(any()) } answers { firstArg() }
        every { itemTagService.createItemTags(any(), any(), any()) } returns listOf()

        When("회원이 핀 정보가 없는 물건을 등록하면") {
            val request = CreateItemRequest(containerId = givenContainer.id, name = "물건 이름", itemType = ItemType.LIFE.name, quantity = 1)
            val itemResponse = itemService.createItem(request, givenMember.id)

            Then("물건이 추가된다") {
                itemResponse.name shouldBe request.name
                itemResponse.containerName shouldBe givenContainer.name
                itemResponse.spaceName shouldBe givenSpace.name
                itemResponse.quantity shouldBe request.quantity
            }
        }

        When("회원이 핀 정보가 있는 물건을 등록하면") {
            val request = CreateItemRequest(containerId = givenContainer.id, name = "물건 이름", itemType = ItemType.LIFE.name, quantity = 1, pinX = 2F, pinY = 3F)

            Then("예외가 발생한다") {
                shouldThrow<BadRequestException> {
                    itemService.createItem(request, givenMember.id)
                }.message
            }
        }
    }
})
