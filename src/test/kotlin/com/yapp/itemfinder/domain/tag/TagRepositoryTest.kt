package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.member.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class TagRepositoryTest(
    private val tagRepository: TagRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    Given("회원이 여러 개의 태그를 등록한 경우") {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenTagIds = mutableListOf<Long>()
        val tagCnt = 3

        repeat(tagCnt) {
            givenTagIds.add(tagRepository.save(FakeEntity.createFakeTagEntity(member = givenMember)).id)
        }

        When("회원 아이디와 태그 아이디로 태그를 조회하면") {
            val tags = tagRepository.findByIdIsInAndMemberId(givenTagIds, givenMember.id)

            Then("태그 정보가 조회된다") {
                tags.size shouldBe tagCnt
                tags.map { it.member.id shouldBe givenMember.id }
            }
        }
    }
})
