package com.yapp.itemfinder.domain.space

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.member.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class SpaceRepositoryTest(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val containerRepository: ContainerRepository,
): BehaviorSpec({

    Given("회원이 여러 공간에 해당하는 보관함을 등록했을 때") {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val firstSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = givenMember))
        val secondSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = givenMember))

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
})
