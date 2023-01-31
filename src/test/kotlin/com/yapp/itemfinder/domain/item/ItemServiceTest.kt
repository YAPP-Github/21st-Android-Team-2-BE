package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SearchTarget
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SearchTarget.SearchLocation
import com.yapp.itemfinder.support.PermissionValidator
import com.yapp.itemfinder.domain.tag.ItemTagService
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class ItemServiceTest : BehaviorSpec({
    val itemRepository = mockk<ItemRepository>(relaxed = true)
    val containerRepository = mockk<ContainerRepository>(relaxed = true)
    val itemTagService = mockk<ItemTagService>(relaxed = true)
    val permissionValidator = mockk<PermissionValidator>(relaxed = true)
    val itemService = ItemService(itemRepository, containerRepository, itemTagService, permissionValidator)

    Given("이미지가 없는 보관함이 등록된 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenSpace = createFakeSpaceEntity(member = givenMember)
        val givenContainer = createFakeContainerEntity(space = givenSpace, imageUrl = null)

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
                itemResponse.imageUrls.isEmpty() shouldBe true
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

    Given("멤버가 물건들을 조회할 때") {
        val (givenSpaceId, givenContainerId, givenMemberId) = Triple(generateRandomPositiveLongValue(), generateRandomPositiveLongValue(), generateRandomPositiveLongValue())
        val givenSpace = createFakeSpaceEntity(id = givenSpaceId)
        val givenContainer = createFakeContainerEntity(space = givenSpace, id = givenContainerId)
        val givenPageRequest = PageRequest.of(0, 20)
        val searchTargetContainerIds = slot<List<Long>>()

        When("공간을 대상으로 해당 공간에 등록된 아이템에 대한 검색을 진행했다면") {
            val (givenFirstContainerIdInSpace, givenSecondContainerIdInSpace) = generateRandomPositiveLongValue() to generateRandomPositiveLongValue()
            val givenSearchOption = ItemSearchOption(
                searchTarget = SearchTarget(location = SearchLocation.SPACE, id = givenSpaceId)
            )

            every { permissionValidator.validateSpaceByMemberId(givenMemberId, givenSpaceId) } returns givenSpace
            every { containerRepository.findBySpace(givenSpace) } returns listOf(
                createFakeContainerEntity(id = givenFirstContainerIdInSpace, space = givenSpace),
                createFakeContainerEntity(id = givenSecondContainerIdInSpace, space = givenSpace)
            )

            itemService.search(givenSearchOption, givenPageRequest, givenMemberId)

            Then("해당 공간에 대한 유저의 권한을 확인한 후, 해당 공간에 속한 보관함 아이디를 찾아 해당 보관함을 대상으로 아이템을 검색한다") {
                verify(exactly = 1) {
                    itemRepository.search(
                        givenSearchOption,
                        givenPageRequest,
                        capture(searchTargetContainerIds)
                    )
                }
                verify(exactly = 1) {
                    permissionValidator.validateSpaceByMemberId(givenMemberId, givenSpaceId)
                }
                searchTargetContainerIds.captured shouldBe listOf(givenFirstContainerIdInSpace, givenSecondContainerIdInSpace)
            }
        }

        When("보관함을 대상으로 해당 보관함에 등록된 아이템에 대한 검색을 진행했다면") {
            val givenSearchOption = ItemSearchOption(
                searchTarget = SearchTarget(location = SearchLocation.CONTAINER, id = givenContainerId)
            )

            every { permissionValidator.validateContainerByMemberId(givenMemberId, givenContainerId) } returns givenContainer

            itemService.search(givenSearchOption, givenPageRequest, givenMemberId)

            Then("해당 보관함에 대한 유저의 권한을 확인한 후, 해당 보관함 아이디를 대상으로 아이템을 검색한다") {
                verify(exactly = 1) {
                    itemRepository.search(
                        givenSearchOption,
                        givenPageRequest,
                        capture(searchTargetContainerIds)
                    )
                }
                searchTargetContainerIds.captured shouldBe listOf(givenContainerId)
            }
        }

        When("공간/보관함 등 특정 위치을 대상으로 아이템 검색을 진행하지 않았다면") {
            val givenSearchOption = ItemSearchOption()
            val givenContainerIdsOfMember = listOf(1L, 2L)
            val givenContainersOfMember = listOf(
                createFakeContainerEntity(id = givenContainerIdsOfMember.first(), space = givenSpace),
                createFakeContainerEntity(id = givenContainerIdsOfMember.last(), space = givenSpace),
            )

            every { containerRepository.findByMemberId(givenMemberId) } returns givenContainersOfMember

            itemService.search(givenSearchOption, givenPageRequest, givenMemberId)

            Then("해당 회원이 등록한 모든 보관함에 등록된 아이템을 대상으로 조회한다") {
                verify(exactly = 1) {
                    itemRepository.search(
                        givenSearchOption,
                        givenPageRequest,
                        capture(searchTargetContainerIds)
                    )
                }
                searchTargetContainerIds.captured shouldBe givenContainerIdsOfMember
            }
        }

        When("검색을 통해 태그가 5개 등록된 아이템이 조회된 경우") {
            val givenSearchOption = ItemSearchOption(
                searchTarget = SearchTarget(location = SearchLocation.CONTAINER, id = givenContainerId)
            )
            val givenItemId = generateRandomPositiveLongValue()
            val givenItem = createFakeItemEntity(id = givenItemId, container = givenContainer)

            every { permissionValidator.validateContainerByMemberId(givenMemberId, givenContainerId) } returns givenContainer
            every { itemRepository.search(givenSearchOption, givenPageRequest, listOf(givenContainerId)) } returns
                PageImpl(listOf(givenItem), givenPageRequest, 1)

            And("찾은 아이템을 대상으로 해당 아이템들의 태그를 찾고") {
                val givenTagNames = listOf("태그1", "태그2", "태그3", "태그4", "태그5")
                every { itemTagService.createItemIdToTagNames(itemIds = listOf(givenItemId)) } returns mapOf(givenItemId to givenTagNames)

                Then("찾은 대그에 대해 아이템 당 최대 4개의 태그까지만 아이템 결과에 담아서 반환한다") {
                    val response = itemService.search(givenSearchOption, givenPageRequest, givenMemberId)
                    assertSoftly {
                        response.totalCount shouldBe 1
                        with(response.data.first()) {
                            tags.size shouldBe 4
                            tags shouldBe givenTagNames.subList(0, 4)
                            name shouldBe givenItem.name
                            id shouldBe givenItemId
                        }
                    }
                }
            }
        }
    }
})
