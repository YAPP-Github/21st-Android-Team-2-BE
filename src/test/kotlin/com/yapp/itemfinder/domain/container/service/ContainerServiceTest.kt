package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.IconType
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ContainerServiceTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>()
    val containerService = ContainerService(containerRepository)

    Given("특정 공간에 보관함이 등록되어 있을 때") {
        val givenSpaceId = generateRandomPositiveLongValue()
        val (givenSpace, givenIconType) = FakeEntity.createFakeSpaceEntity(id = givenSpaceId) to IconType.IC_CONTAINER_2
        val givenContainer = FakeEntity.createFakeContainerEntity(space = givenSpace, iconType = givenIconType)
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
})
