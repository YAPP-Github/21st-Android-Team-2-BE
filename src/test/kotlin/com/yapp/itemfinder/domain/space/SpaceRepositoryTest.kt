package com.yapp.itemfinder.domain.space

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.member.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity as createFakeMemberEntity1

@RepositoryTest
class SpaceRepositoryTest(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val containerRepository: ContainerRepository,
) : BehaviorSpec({

    Given("회원이 여러 공간에 해당하는 보관함을 등록했을 때") {
        val givenMember = memberRepository.save(createFakeMemberEntity1())
        val firstSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))
        val secondSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))

        val (firstSpaceCount, secondSpaceCount) = 3 to 2

        repeat(firstSpaceCount) {
            containerRepository.save(FakeEntity.createFakeContainerEntity(space = firstSpace))
        }

        repeat(secondSpaceCount) {
            containerRepository.save(FakeEntity.createFakeContainerEntity(space = secondSpace))
        }

        When("조회하고자 하는 대상 멤버 아이디를 전달한다면") {
            val result = spaceRepository.getSpaceWithContainerCountByMemberId(givenMember.id)

            Then("해당 회원이 등록한 각 공간에 등록된 보관함 개수를 확인할 수 있고, 각 공간들은 생성된 순서대로 조회된다") {
                result.size shouldBe 2
                with(result.first()) {
                    containerCount shouldBe 3
                    spaceId shouldBe firstSpace.id
                    spaceName shouldBe firstSpace.name
                }

                with(result.last()) {
                    containerCount shouldBe 2
                    spaceId shouldBe secondSpace.id
                    spaceName shouldBe secondSpace.name
                }
            }
        }
    }

    Given("회원이 특정 공간을 저장했을 때") {
        val givenMember = memberRepository.save(createFakeMemberEntity1())
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))

        When("해당하는 회원 아이디와 저장한 공간 아이디로 공간을 조회한다면") {
            val result = spaceRepository.findByIdAndMemberId(givenSpace.id, givenMember.id)

            Then("저장한 공간이 조회된다") {
                result shouldBe givenSpace
            }
        }

        When("해당하는 회원 아이디와 저장하지 않은 공간 아이디로 공간을 조회하면") {
            val anotherSpaceId = generateRandomPositiveLongValue()
            val result = spaceRepository.findByIdAndMemberId(anotherSpaceId, givenMember.id)

            Then("공간이 조회되지 않는다") {
                result shouldBe null
            }
        }
    }

    Given("특정 회원이 특정 공간을 등록했을 때") {
        val givenMember = memberRepository.save(createFakeMemberEntity1())
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))
        val (givenSavedMemberId, givenSavedSpaceId) = givenMember.id to givenSpace.id

        When("해당 회원이 등록하지 않은 공간에 대해 조회한다면") {
            val otherOtherSpaceId = generateRandomPositiveLongValue()

            Then("조회된 공간이 없으므로 예외가 발생한다") {
                shouldThrow<BadRequestException> {
                    spaceRepository.findByIdAndMemberIdOrThrowException(id = otherOtherSpaceId, memberId = givenSavedMemberId)
                }
            }
        }

        When("해당 회원이 등록한 공간에 대해 조회한다면") {
            val result = spaceRepository.findByIdAndMemberIdOrThrowException(id = givenSavedSpaceId, memberId = givenSavedMemberId)

            Then("조회된 공간이 있으므로 해당하는 공간의 정보를 반환한다") {
                result shouldBe givenSpace
            }
        }
    }
})
