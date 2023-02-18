package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.DEFAULT_CONTAINER_NAME
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.IconType
import com.yapp.itemfinder.domain.container.dto.CreateContainerRequest
import com.yapp.itemfinder.domain.container.dto.UpdateContainerRequest
import com.yapp.itemfinder.domain.item.ItemService
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.space.findByIdAndMemberIdOrThrowException
import com.yapp.itemfinder.support.PermissionValidator
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class ContainerServiceTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>(relaxed = true)
    val spaceRepository = mockk<SpaceRepository>(relaxed = true)
    val permissionValidator = mockk<PermissionValidator>(relaxed = true)
    val itemService = mockk<ItemService>()
    val containerService = ContainerService(containerRepository, spaceRepository, permissionValidator, itemService)

    Given("특정 공간에 보관함이 등록되어 있을 때") {
        val givenSpaceId = generateRandomPositiveLongValue()
        val (givenSpace, givenIconType) = createFakeSpaceEntity(id = givenSpaceId) to IconType.IC_CONTAINER_2
        val givenContainer = createFakeContainerEntity(space = givenSpace, iconType = givenIconType)
        every { containerRepository.findBySpaceIdIsIn(listOf(givenSpaceId)) } returns listOf(givenContainer)

        When("전달받은 공간 아이디 리스트에 대한 보관함 정보들을 조회했다면") {
            val result = containerService.getSpaceIdToContainers(listOf(givenSpaceId))

            Then("해당 아이콘의 이름을 map 형태(spaceId: 키, 보관한 엔티티: 값)로 변환해서 반환한다") {
                assertSoftly {
                    result.keys.size shouldBe 1
                    result[givenSpaceId]?.size shouldBe 1
                    result[givenSpaceId]?.first()?.let {
                        it.id shouldBe givenContainer.id
                        it.spaceId shouldBe givenSpaceId
                        it.iconType shouldBe givenContainer.iconType.name
                        it.name shouldBe givenContainer.name
                        it.imageUrl shouldBe givenContainer.imageUrl
                    }
                }
            }
        }

        And("존재하는 보관함에 대한 정보를 수정할 경우") {
            val (givenName, givenIcon, givenUrl) = Triple(generateRandomString(4), IconType.IC_CONTAINER_7, generateRandomString(10))
            val (currentSpaceId, givenMemberId) = givenContainer.space.id to givenSpace.member.id

            When("보관함의 위치는 수정하지 않고 보관함 자체에 대한 정보(이름, 아이콘, 이미지)만 수정할 경우") {
                val givenUpdateRequest = UpdateContainerRequest(spaceId = currentSpaceId, name = givenName, icon = givenIcon.name, url = givenUrl)

                And("수정하려는 보관함에 대한 권한이 있고, 해당 공간에 동일한 보관한 명으로 존재하는 보관함이 존재하지 않는다면") {
                    every { permissionValidator.validateContainerByMemberId(givenMemberId, givenContainer.id) } returns givenContainer
                    every { containerRepository.findBySpaceIdAndName(currentSpaceId, givenName) } returns null

                    Then("보관함이 저장되는 공간은 변하지 않았으므로 공간 정보는 별도로 찾지 않고 전달받은 정보로 보관함 업데이트를 진행한다") {
                        val response = containerService.updateContainer(givenMemberId, givenContainer.id, givenUpdateRequest)
                        response.name shouldBe givenName
                        response.icon shouldBe givenIcon.name
                        response.imageUrl shouldBe givenUrl
                        response.spaceId shouldBe currentSpaceId

                        verify(exactly = 0) {
                            permissionValidator.validateSpaceByMemberId(any(), any())
                        }
                    }
                }

                And("수정하려는 보관함에 대한 권한이 있고, 기존 보관함명이 아닌 새로운 보관함 명으로 수정할 경우") {
                    val currentContainerName = "기존 이름"
                    val currentContainer = createFakeContainerEntity(space = givenSpace, name = currentContainerName)

                    every { permissionValidator.validateContainerByMemberId(givenMemberId, currentContainer.id) } returns currentContainer

                    Then("해당 공간에 동일한 보관함 명으로 존재하는 보관함이 존재하는지 확인하고, 이미 존재한다면 요청한 보관함 이름으로 보관함을 수정할 수 없다") {
                        every { containerRepository.findBySpaceIdAndName(currentContainer.space.id, givenName) } returns givenContainer

                        currentContainerName shouldNotBe givenUpdateRequest.name

                        shouldThrow<ConflictException> {
                            containerService.updateContainer(givenMemberId, currentContainer.id, givenUpdateRequest)
                        }
                    }
                }

                And("수정하려는 보관함에 대한 권한이 있고, 기존 보관함 명을 새로운 보관함 명으로 수정하지 않는다면") {
                    val currentContainer = createFakeContainerEntity(space = givenSpace, name = givenUpdateRequest.name)
                    every { permissionValidator.validateContainerByMemberId(givenMemberId, currentContainer.id) } returns currentContainer

                    Then("해당 공간에 동일한 보관함 명으로 존재하는 보관함이 존재하는지 검증을 진행하지 않고 그 외 정보(아이콘, 이미지)에 대한 수정을 진행한다") {
                        val response = containerService.updateContainer(givenMemberId, currentContainer.id, givenUpdateRequest)

                        verify(exactly = 0) {
                            containerRepository.findBySpaceIdAndName(any(), any())
                        }

                        currentContainer.name shouldBe givenUpdateRequest.name

                        response.name shouldBe currentContainer.name
                        response.icon shouldBe givenIcon.name
                        response.imageUrl shouldBe givenUrl
                        response.spaceId shouldBe currentSpaceId
                    }
                }
            }

            When("보관함이 위치하는 공간 정보 또한 수정한다면") {
                val newSpaceId = generateRandomPositiveLongValue()
                val givenUpdateRequest = UpdateContainerRequest(spaceId = newSpaceId, name = givenName, icon = givenIcon.name, url = givenUrl)

                And("수정하려는 보관함에 대한 권한이 있고, 해당 공간에 동일한 보관한 명으로 존재하는 보관함이 존재하지 않는다면") {
                    every { permissionValidator.validateContainerByMemberId(givenMemberId, givenContainer.id) } returns givenContainer
                    every { containerRepository.findBySpaceIdAndName(newSpaceId, givenName) } returns null

                    Then("보관함이 저장되는 공간이 변했으므로 해당 공간에 대한 검증 또한 진행한 후, 전달받은 정보로 업데이트를 진행한다") {
                        every { permissionValidator.validateSpaceByMemberId(givenMemberId, newSpaceId) } returns createFakeSpaceEntity(id = newSpaceId)

                        val response = containerService.updateContainer(givenMemberId, givenContainer.id, givenUpdateRequest)
                        response.name shouldBe givenName
                        response.icon shouldBe givenIcon.name
                        response.imageUrl shouldBe givenUrl
                        response.spaceId shouldBe newSpaceId
                    }
                }
            }
        }
    }

    Given("새로운 공간이 생성되었을 때") {
        val givenSpace = createFakeSpaceEntity()
        val givenContainer = createFakeContainerEntity(space = givenSpace)
        val containerCaptor = slot<ContainerEntity>()

        every { containerRepository.save(capture(containerCaptor)) } returns givenContainer

        When("해당 공간이 주어진다면") {
            containerService.addDefaultContainer(newSpace = givenSpace)

            Then("해당 공간에 대한 디폴트 보관함(기본 이름: 보관함, 기본 아이콘: 아이콘1) 을 생성 후 저장한다") {
                containerCaptor.captured.space shouldBe givenSpace
                containerCaptor.captured.name shouldBe DEFAULT_CONTAINER_NAME
                containerCaptor.captured.name shouldBe "보관함"
                containerCaptor.captured.iconType shouldBe IconType.IC_CONTAINER_1
            }
        }
    }

    Given("공간에 등록된 보관함을 조회할 때") {
        val (givenMemberId, givenSpaceId) = generateRandomPositiveLongValue() to generateRandomPositiveLongValue()

        When("유저가 전달한 공간 아이디로 실제 등록된 공간이 존재한다면") {
            val givenSpace = createFakeSpaceEntity(id = givenSpaceId)
            val givenContainer = createFakeContainerEntity(space = givenSpace)

            every { spaceRepository.findByIdAndMemberId(id = givenSpaceId, memberId = givenMemberId) } returns givenSpace
            every { containerRepository.findBySpaceOrderByCreatedAtAsc(givenSpace) } returns listOf(givenContainer)

            val response = containerService.findContainersInSpace(requestMemberId = givenMemberId, spaceId = givenSpaceId)

            Then("해당하는 공간에 등록된 보관함 정보들을 반환한다") {
                response.size shouldBe 1
                with(response.first()) {
                    id shouldBe givenContainer.id
                    icon shouldBe givenContainer.iconType.name
                    spaceId shouldBe givenContainer.space.id
                    name shouldBe givenContainer.name
                    imageUrl shouldBe givenContainer.imageUrl
                }
            }
        }
    }

    Given("신규 보관함을 공간에 등록할 때") {
        val (givenMemberId, givenSpaceId) = generateRandomPositiveLongValue() to generateRandomPositiveLongValue()
        val givenMember = createFakeMemberEntity(id = givenMemberId)
        val (givenSpace, givenIconType) = createFakeSpaceEntity(id = givenSpaceId, member = givenMember) to IconType.IC_CONTAINER_5

        val givenCreateContainerRequest = CreateContainerRequest(
            spaceId = givenSpaceId,
            name = "name",
            _icon = givenIconType.name,
            url = "https://cdn.pixabay.com/photo/2016/03/28/12/35/cat-1285634_1280.png"
        )

        every { spaceRepository.findByIdAndMemberIdOrThrowException(givenSpaceId, givenMemberId) } returns givenSpace

        When("요청한 보관함명과 동일한 이름으로 등록된 보관함이 이미 존재한다면") {
            every { containerRepository.findBySpaceIdAndName(givenSpaceId, givenCreateContainerRequest.name) } returns createFakeContainerEntity(space = givenSpace)

            Then("해당 보관함을 추가할 수 없다") {
                shouldThrow<ConflictException> {
                    containerService.createContainer(givenMemberId, givenCreateContainerRequest)
                }
            }
        }

        When("요청한 보관함명과 동일한 이름으로 등록된 보관함이 이미 존재하지 않는다면") {
            val containerCaptor = slot<ContainerEntity>()

            every { containerRepository.findBySpaceIdAndName(givenSpaceId, givenCreateContainerRequest.name) } returns null
            every { containerRepository.save(capture(containerCaptor)) } returns createFakeContainerEntity(space = givenSpace)

            Then("해당 보관함을 공간에 추가할 수 있다") {
                assertSoftly {
                    containerService.createContainer(givenMemberId, givenCreateContainerRequest)
                    with(containerCaptor.captured) {
                        space.id shouldBe givenSpaceId
                        name shouldBe givenCreateContainerRequest.name
                        iconType shouldBe givenIconType
                        imageUrl shouldBe givenCreateContainerRequest.url
                    }
                }
            }
        }
    }

    Given("공간에 보관함이 1개 등록되어 있는 경우") {
        val givenMember = createFakeMemberEntity()
        val givenSpace = createFakeSpaceEntity(member = givenMember)
        val givenContainer = createFakeContainerEntity(space = givenSpace)

        every { permissionValidator.validateContainerByMemberId(givenMember.id, givenContainer.id) } returns givenContainer
        every { containerRepository.countBySpace(givenSpace) } returns 1L

        When("보관함을 삭제하면") {
            Then("예외가 발생한다") {
                shouldThrow<BadRequestException> {
                    containerService.deleteContainer(givenMember.id, givenContainer.id)
                }
            }
        }
    }
})
