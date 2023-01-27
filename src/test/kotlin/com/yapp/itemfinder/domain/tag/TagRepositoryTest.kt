package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.member.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

@RepositoryTest
class TagRepositoryTest(
    private val tagRepository: TagRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    Given("회원이 여러 개의 태그를 등록한 경우") {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenTags = mutableListOf<TagEntity>()
        val tagCnt = 3

        repeat(tagCnt) {
            givenTags.add(tagRepository.save(FakeEntity.createFakeTagEntity(member = givenMember)))
        }

        When("회원 아이디와 태그 아이디로 태그를 조회하면") {
            val tags = tagRepository.findByIdIsInAndMemberId(givenTags.map { it.id }, givenMember.id)

            Then("태그 정보가 조회된다") {
                tags.size shouldBe tagCnt
                tags.map { it.member.id shouldBe givenMember.id }
            }
        }

        When("회원의 태그를 조회하면") {
            val tags = tagRepository.findByNameIsInAndMember(givenTags.map { it.name }, givenMember)

            Then("태그가 조회된다") {
                tags.size shouldBe 3
                tags shouldContainAll givenTags
            }
        }
    }

    Given("회원이 등록하지 않은 경우") {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val name = "태그 이름"

        When("회원의 태그를 조회하면") {
            val tag = tagRepository.findByNameIsInAndMember(listOf(name), givenMember)

            Then("태그가 조회되지 않는다") {
                tag shouldBe emptyList()
            }
        }
    }
})
