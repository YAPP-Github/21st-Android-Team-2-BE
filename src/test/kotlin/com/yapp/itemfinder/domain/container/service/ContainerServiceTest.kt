package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.DEFAULT_CONTAINER_NAME
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.IconType
import com.yapp.itemfinder.domain.container.dto.CreateContainerRequest
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.space.findByIdAndMemberIdOrThrowException
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class ContainerServiceTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>(relaxed = true)
    val spaceRepository = mockk<SpaceRepository>(relaxed = true)
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
            url = "https://cdn.pixabay.com/photo/2016/03/28/12/35/cat-1285634_1280.png",
        )

        every { spaceRepository.findByIdAndMemberIdOrThrowException(givenSpaceId, givenMemberId) } returns givenSpace

        When("요청한 보관함명과 동일한 이름으로 등록된 보관함이 이미 존재한다면") {
            every { containerRepository.findBySpaceIdAndName(givenSpaceId, givenCreateContainerRequest.name) } returns createFakeContainerEntity(space = givenSpace)

            Then("해당 보관함을 추가할 수 있다") {
                shouldThrow<ConflictException> {
                    containerService.createContainer(givenMemberId, givenCreateContainerRequest)
                }
            }
        }

        When("요청한 보관함명과 동일한 이름으로 등록된 보관함이 이미 존재하지 않는다면") {
            val containerCaptor = slot<ContainerEntity>()

            every { containerRepository.findBySpaceIdAndName(givenSpaceId, givenCreateContainerRequest.name) } returns null
            every { containerRepository.save(capture(containerCaptor)) } returns createFakeContainerEntity(space = givenSpace)

            Then("해당 보관함을 공간에 추가할 수 없다") {
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
})
