package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.DEFAULT_CONTAINER_NAME
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.IconType
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class ContainerServiceTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>()
    val containerService = ContainerService(containerRepository)

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
})
