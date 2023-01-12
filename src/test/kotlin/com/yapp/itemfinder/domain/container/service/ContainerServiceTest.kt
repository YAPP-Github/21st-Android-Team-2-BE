package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.DEFAULT_CONTAINER_NAME
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.IconType
import com.yapp.itemfinder.domain.space.SpaceRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class ContainerServiceTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>()
    val spaceRepository = mockk<SpaceRepository>()
    val containerService = ContainerService(containerRepository, spaceRepository)

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
                        it.defaultItemType shouldBe givenContainer.defaultItemType.name
                        it.description shouldBe givenContainer.description
                        it.imageUrl shouldBe givenContainer.imageUrl
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

        When("요청한 유저가 전달한 공간 아이디로 실제 등록된 공간이 존재하지 않는다면") {
            every { spaceRepository.findByIdAndMemberId(id = givenSpaceId, memberId = givenMemberId) } returns null

            Then("예외가 발생한다") {
                shouldThrow<BadRequestException> {
                    containerService.findContainersInSpace(requestMemberId = givenMemberId, spaceId = givenSpaceId)
                }
            }
        }

        When("요청한 유저가 전달한 공간 아이디로 실제 등록된 공간이 존재한다면") {
            val givenSpace = createFakeSpaceEntity(id = givenSpaceId)
            val givenContainer = createFakeContainerEntity(space = givenSpace)

            every { spaceRepository.findByIdAndMemberId(id = givenSpaceId, memberId = givenMemberId) } returns givenSpace
            every { containerRepository.findBySpaceOrderByCreatedAtAsc(givenSpace) } returns listOf(givenContainer)

            val response = containerService.findContainersInSpace(requestMemberId = givenMemberId, spaceId = givenSpaceId)

            Then("해당하는 보관함 정보를 반환한다") {
                response.size shouldBe 1
                with(response.first()) {
                    id shouldBe givenContainer.id
                    icon shouldBe givenContainer.iconType.name
                    spaceId shouldBe givenContainer.space.id
                    name shouldBe givenContainer.name
                    defaultItemType shouldBe givenContainer.defaultItemType.name
                    description shouldBe givenContainer.description
                    imageUrl shouldBe givenContainer.imageUrl
                }
            }
        }
    }
})
