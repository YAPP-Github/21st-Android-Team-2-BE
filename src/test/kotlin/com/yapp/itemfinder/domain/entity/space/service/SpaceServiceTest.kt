package com.yapp.itemfinder.domain.entity.space.service

import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.entity.space.SpaceEntity
import com.yapp.itemfinder.domain.entity.space.SpaceRepository
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class SpaceServiceTest : BehaviorSpec({
    val spaceRepository = mockk<SpaceRepository>()
    val spaceService = SpaceService(spaceRepository)

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
            val spaceCaptor = slot<SpaceEntity>()
            every { spaceRepository.findByMemberIdAndName(givenMember.id, givenSpaceName) } returns null
            every { spaceRepository.save(capture(spaceCaptor)) } returns createFakeSpaceEntity(name = givenSpaceName, member = givenMember)

            Then("정상적으로 해당 공간을 등록할 수 있다") {
                shouldNotThrow<Exception> {
                    spaceService.createSpace(givenCreateSpaceRequest, givenMember)
                }
                spaceCaptor.captured.name shouldBe givenSpaceName
                spaceCaptor.captured.member.id shouldBe givenMember.id
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
})
