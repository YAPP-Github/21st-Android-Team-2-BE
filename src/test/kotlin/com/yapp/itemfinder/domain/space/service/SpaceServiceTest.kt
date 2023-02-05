package com.yapp.itemfinder.domain.space.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.container.IconType.IC_CONTAINER_2
import com.yapp.itemfinder.domain.container.IconType.IC_CONTAINER_3
import com.yapp.itemfinder.domain.container.IconType.IC_CONTAINER_4
import com.yapp.itemfinder.domain.container.IconType.IC_CONTAINER_5
import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.container.service.ContainerService
import com.yapp.itemfinder.domain.container.service.ContainerVo
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.SpaceWithContainerCount
import com.yapp.itemfinder.domain.space.dto.UpdateSpaceRequest
import com.yapp.itemfinder.support.PermissionValidator
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class SpaceServiceTest : BehaviorSpec({
    val spaceRepository = mockk<SpaceRepository>()
    val containerService = mockk<ContainerService>(relaxed = true)
    val permissionValidator = mockk<PermissionValidator>(relaxed = true)

    val spaceService = SpaceService(spaceRepository, containerService, permissionValidator)

    Given("공간을 새로 추가할 때") {
        val givenSpaceName = "공간 이름1"
        val givenMember = createFakeMemberEntity()
        val givenCreateSpaceRequest = CreateSpaceRequest(name = givenSpaceName)

        When("이미 요청한 공간명으로 해당 유저가 등록한 공간이 존재한다면") {
            every {
                spaceRepository.findByMemberIdAndName(givenMember.id, givenSpaceName)
            } returns createFakeSpaceEntity(name = givenSpaceName, member = givenMember)

            Then("예외를 발생시킨다") {
                shouldThrow<ConflictException> {
                    spaceService.createSpace(givenCreateSpaceRequest, givenMember)
                }

                verify(exactly = 0) {
                    spaceRepository.save(any())
                }
            }
        }

        When("요청한 공간명으로 해당 유저가 등록한 공간이 존재하지 않는다면") {
            val givenSpace = createFakeSpaceEntity(name = givenSpaceName, member = givenMember)
            val spaceCaptor = slot<SpaceEntity>()

            every { spaceRepository.findByMemberIdAndName(givenMember.id, givenSpaceName) } returns null
            every { spaceRepository.save(capture(spaceCaptor)) } returns givenSpace

            Then("정상적으로 해당 공간을 등록하고 기본 보관함 생성을 요청할 수 있다") {
                val response = spaceService.createSpace(givenCreateSpaceRequest, givenMember)

                spaceCaptor.captured.name shouldBe givenSpaceName
                spaceCaptor.captured.member.id shouldBe givenMember.id

                response.name shouldBe givenCreateSpaceRequest.name
                response.id shouldBe givenSpace.id

                verify(exactly = 1) {
                    containerService.addDefaultContainer(givenSpace)
                }
            }
        }
    }

    Given("유저가 본인이 등록한 공간을 조회할 때") {
        val givenMemberId = generateRandomPositiveLongValue()

        When("등록한 공간이 없으면") {
            every { spaceRepository.findByMemberId(givenMemberId) } returns emptyList()

            Then("빈 공간 정보 리스트를 담은 값을 반환한다") {
                val result = spaceService.getSpaces(givenMemberId)
                result.spaces shouldBe emptyList()
            }
        }

        When("등록한 공간이 있으면") {
            val (givenSpaceId, givenSpaceName, givenMember) = Triple(generateRandomPositiveLongValue(), "공간명", createFakeMemberEntity())
            val givenSpace = createFakeSpaceEntity(id = givenSpaceId, name = givenSpaceName, member = givenMember)
            every { spaceRepository.findByMemberId(givenMemberId) } returns listOf(givenSpace)

            Then("해당 공간 정보(id, 이름) 리스트를 담은 값을 반환한다") {
                val result = spaceService.getSpaces(givenMemberId)

                result.spaces.size shouldBe 1
                result.spaces.first().id shouldBe givenSpaceId
                result.spaces.first().name shouldBe givenSpaceName
            }
        }
    }

    Given("유저가 홈 뷰에서 본인이 등록한 공간과 대표 보관함 정보들을 확인할 때") {
        val givenMemberId = generateRandomPositiveLongValue()
        val (givenSpaceId, givenSpaceName, givenContainerCount) = Triple(generateRandomPositiveLongValue(), "공간 이름", 5L)

        val givenContainerVos = mutableListOf<ContainerVo>()

        every {
            spaceRepository.getSpaceWithContainerCountByMemberId(givenMemberId)
        } returns listOf(SpaceWithContainerCount(givenSpaceId, givenSpaceName, givenContainerCount))

        When("유저가 등록한 공간에 보관함이 5개 이상 있다면") {
            listOf(IC_CONTAINER_2, IC_CONTAINER_2, IC_CONTAINER_3, IC_CONTAINER_4, IC_CONTAINER_5).forEach {
                createFakeContainerEntity(
                    iconType = it,
                    space = createFakeSpaceEntity(id = givenSpaceId)
                ).apply {
                    givenContainerVos.add(ContainerVo(this))
                }
            }
            every { containerService.getSpaceIdToContainers(listOf(givenSpaceId)) } returns mapOf(givenSpaceId to givenContainerVos)

            val result = spaceService.getSpaceWithTopContainers(givenMemberId)

            Then("전달받은 보관함 정보들을 모두 전달하지 않고 중 최대 4개까지만 제한해서 전달한다") {
                assertSoftly {
                    result.size shouldBe 1
                    with(result.first()) {
                        spaceId shouldBe givenSpaceId
                        spaceName shouldBe givenSpaceName
                        containerCount shouldBe givenContainerCount

                        topContainers.size shouldBe 4
                        topContainers[0] shouldBe ContainerResponse(givenContainerVos[0])
                        topContainers[1] shouldBe ContainerResponse(givenContainerVos[1])
                        topContainers[2] shouldBe ContainerResponse(givenContainerVos[2])
                        topContainers[3] shouldBe ContainerResponse(givenContainerVos[3])
                    }
                }
            }
        }

        When("유저가 등록한 공간에 보관함이 4개 이하 있다면") {
            givenContainerVos.clear()
            givenContainerVos.add(
                ContainerVo(
                    createFakeContainerEntity(iconType = IC_CONTAINER_2, space = createFakeSpaceEntity(id = givenSpaceId))
                )
            )

            every {
                containerService.getSpaceIdToContainers(listOf(givenSpaceId))
            } returns mapOf(
                givenSpaceId to givenContainerVos
            )

            val result = spaceService.getSpaceWithTopContainers(givenMemberId)

            Then("전달받은 보관함 정보들을 모두 전달한다") {
                assertSoftly {
                    result.size shouldBe 1
                    with(result.first()) {
                        spaceId shouldBe givenSpaceId
                        spaceName shouldBe givenSpaceName
                        containerCount shouldBe givenContainerCount

                        topContainers.size shouldBe 1
                        topContainers[0].id shouldBe givenContainerVos[0].id
                        topContainers[0].icon shouldBe givenContainerVos[0].iconType
                        topContainers[0].imageUrl shouldBe givenContainerVos[0].imageUrl
                        topContainers[0].spaceId shouldBe givenContainerVos[0].spaceId
                    }
                }
            }
        }
    }

    Given("기존에 등록한 공간을 수정할 때") {
        val givenNewSpaceName = "새로운 공간 이름"
        val givenMember = createFakeMemberEntity()
        val givenUpdateSpaceRequest = UpdateSpaceRequest(name = givenNewSpaceName)
        val givenExistSpace = createFakeSpaceEntity()

        When("유저가 요청한 새로운 공간명으로 이미 해당 유저가 등록한 공간이 존재한다면") {
            every {
                spaceRepository.findByMemberIdAndName(givenMember.id, givenNewSpaceName)
            } returns createFakeSpaceEntity(name = givenNewSpaceName, member = givenMember)

            Then("예외를 발생시킨다") {
                shouldThrow<ConflictException> {
                    spaceService.updateSpace(
                        memberId = givenMember.id,
                        spaceId = givenExistSpace.id,
                        spaceName = givenUpdateSpaceRequest.name
                    )
                }

                verify(exactly = 0) {
                    permissionValidator.validateSpaceByMemberId(any(), any())
                    givenExistSpace.updateSpace(givenNewSpaceName)
                }
            }
        }

        When("해당 유저가 요청한 새로운 공간명으로 해당 유저가 등록한 공간이 존재하지 않는다면") {
            And("해당 공간에 대한 유저의 권한을 확인하고") {
                every {
                    permissionValidator.validateSpaceByMemberId(memberId = givenMember.id, spaceId = givenExistSpace.id)
                } returns givenExistSpace

                Then("공간에 대한 권한이 있다면(해당 유저가 등록한 공간임) 해당 공간명을 수정할 수 있다") {
                    val response = givenExistSpace.updateSpace(givenNewSpaceName)
                    response.id shouldBe givenExistSpace.id
                    response.name shouldBe givenNewSpaceName
                }
            }
        }
    }
})
