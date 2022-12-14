package com.yapp.itemfinder.domain.container

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ContainerRepositoryTest(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val containerRepository: ContainerRepository,
) : BehaviorSpec({

    Given("회원이 여러 공간에 해당하는 보관함을 등록했을 때") {
        val givenMember = memberRepository.save(createFakeMemberEntity())
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))
        val givenIconTypes = listOf(IconType.IC_CONTAINER_3, IconType.IC_CONTAINER_4)
        val givenContainers = mutableListOf<ContainerEntity>()

        repeat(2) {
            containerRepository.save(
                createFakeContainerEntity(space = givenSpace, iconType = givenIconTypes[it])
            ).also { container ->
                givenContainers.add(container)
            }
        }

        When("등록한 공간 아이디 리스트를 전달하면") {
            val result = containerRepository.findBySpaceIdIsIn(listOf(givenSpace.id))

            Then("해당 공간들에 속한 보관함 정보들을 찾아 반환한다") {
                result.size shouldBe 2
                with(result.first()) {
                    space.id shouldBe givenSpace.id
                    iconType shouldBe givenIconTypes.first()
                    id shouldBe givenContainers.first().id
                }

                with(result.last()) {
                    space.id shouldBe givenSpace.id
                    iconType shouldBe givenIconTypes.last()
                    id shouldBe givenContainers.last().id
                }
            }
        }
    }
})
