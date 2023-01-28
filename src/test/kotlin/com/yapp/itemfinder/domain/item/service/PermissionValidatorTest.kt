package com.yapp.itemfinder.domain.item.service

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.ForbiddenException
import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.support.PermissionValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

class PermissionValidatorTest : BehaviorSpec({
    val containerRepository = mockk<ContainerRepository>()
    val spaceRepository = mockk<SpaceRepository>()
    val permissionValidator = PermissionValidator(containerRepository, spaceRepository)

    Given("멤버가 한 공간을 등록한 경우") {
        val givenMember = createFakeMemberEntity()
        val givenSpace = createFakeSpaceEntity(member = givenMember)
        val (givenMemberId, givenSpaceId) = givenMember.id to givenSpace.id

        When("등록하지 않은 공간으로 유저가 헤딩 공간에 대한 권한을 검즐한다면") {
            val givenNonExistSpaceId = generateRandomPositiveLongValue()
            every { spaceRepository.findByIdOrNull(givenNonExistSpaceId) } returns null

            Then("예외가 발생한다") {
                shouldThrow<NotFoundException> {
                    permissionValidator.validateSpaceByMemberId(
                        memberId = givenMemberId,
                        spaceId = givenNonExistSpaceId
                    )
                }
            }
        }

        When("존재하는 공간이지만 요청한 유저가 등록한 공간이 아닌 상황에서 권한을 검즐한다면") {
            every { spaceRepository.findByIdOrNull(givenSpaceId) } returns givenSpace

            val givenAnotherMemberId = generateRandomPositiveLongValue()

            Then("예외가 발생한다") {
                shouldThrow<ForbiddenException> {
                    permissionValidator.validateSpaceByMemberId(
                        memberId = givenAnotherMemberId,
                        spaceId = givenSpaceId
                    )
                }
            }
        }

        When("해당 공간이 존재하고, 요청한 유저가 등록한 공간인 상황에서 권한을 검증한다면") {
            every { spaceRepository.findByIdOrNull(givenSpaceId) } returns givenSpace

            val result = permissionValidator.validateSpaceByMemberId(
                memberId = givenMemberId,
                spaceId = givenSpaceId
            )

            Then("검증을 통과한 해당 공간을 반환한다") {
                result shouldBe givenSpace
            }
        }
    }

    Given("멤버가 한 보관함을 등록한 경우") {
        val givenMember = createFakeMemberEntity()
        val givenContainer = createFakeContainerEntity(space = createFakeSpaceEntity(member = givenMember))
        val (givenMemberId, givenContainerId) = givenMember.id to givenContainer.id

        When("존재하지 않은 보관함으로 유저가 등록했는지에 대해 권한을 검증한 경우") {
            val givenNonExistContainerId = generateRandomPositiveLongValue()
            every { containerRepository.findByIdWithSpace(givenNonExistContainerId) } returns null

            Then("예외가 발생한다") {
                shouldThrow<NotFoundException> {
                    permissionValidator.validateContainerByMemberId(
                        memberId = givenMemberId,
                        containerId = givenNonExistContainerId
                    )
                }
            }
        }

        When("해당 보관함이 존재하지만 요청한 유저가 등록한 보관함이 아니라면") {
            every { containerRepository.findByIdWithSpace(givenContainerId) } returns givenContainer

            val givenAnotherMemberId = generateRandomPositiveLongValue()

            Then("예외가 발생한다") {
                shouldThrow<ForbiddenException> {
                    permissionValidator.validateContainerByMemberId(
                        memberId = givenAnotherMemberId,
                        containerId = givenContainerId
                    )
                }
            }
        }

        When("해당 보관함이 존재하고, 요청한 유저가 등록한 보관함이라면") {
            every { containerRepository.findByIdWithSpace(givenContainerId) } returns givenContainer

            val result = permissionValidator.validateContainerByMemberId(
                memberId = givenMemberId,
                containerId = givenContainerId
            )

            Then("검증을 통과한 보관함을 반환한다") {
                result shouldBe givenContainer
            }
        }
    }
})
