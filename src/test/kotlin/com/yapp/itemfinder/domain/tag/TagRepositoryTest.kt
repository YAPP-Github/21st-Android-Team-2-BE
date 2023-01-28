package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeTagEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.member.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.PageRequest

@RepositoryTest
class TagRepositoryTest(
    private val tagRepository: TagRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    Given("회원이 여러 개의 태그를 등록한 경우") {
        val givenMember = memberRepository.save(createFakeMemberEntity())
        val givenTags = mutableListOf<TagEntity>()
        val tagCnt = 11

        repeat(tagCnt) {
            givenTags.add(tagRepository.save(createFakeTagEntity(member = givenMember)))
        }

        When("회원 아이디와 태그 아이디로 태그를 조회하면") {
            val tags = tagRepository.findByIdIsInAndMemberId(givenTags.map { it.id }, givenMember.id)

            Then("태그 정보가 조회된다") {
                tags.size shouldBe tagCnt
                tags.map { it.member.id shouldBe givenMember.id }
            }
        }

        When("회원의 태그를 이름으로 조회하면") {
            val tags = tagRepository.findByNameIsInAndMember(givenTags.map { it.name }, givenMember)

            Then("태그가 조회된다") {
                tags.size shouldBe tagCnt
                tags shouldContainAll givenTags
            }
        }

        When("회원의 전체 태그 목록을 최신순으로 조회하면") {
            val tags = tagRepository.findByMemberOrderByCreatedAtDesc(givenMember)

            Then("전체 태그가 최신순으로 조회된다") {
                tags.size shouldBe tagCnt
                tags shouldContainInOrder givenTags.reversed()
            }
        }

        When("회원의 태그를 최신순으로 상위 10개 조회하면") {
            val pageRequest = PageRequest.of(0, 10)
            val tagPage = tagRepository.findByMemberOrderByCreatedAtDesc(givenMember, pageRequest)

            Then("태그가 최신순으로 최대 10개까지 조회된다") {
                tagPage.totalElements shouldBe tagCnt
                tagPage.totalPages shouldBe 2
                tagPage.content.size shouldBe 10
                tagPage.content shouldContainInOrder givenTags.reversed().subList(0, 9)
            }
        }
    }

    Given("회원이 등록하지 않은 경우") {
        val givenMember = memberRepository.save(createFakeMemberEntity())
        val name = "태그 이름"

        When("회원의 태그를 조회하면") {
            val tag = tagRepository.findByNameIsInAndMember(listOf(name), givenMember)

            Then("태그가 조회되지 않는다") {
                tag shouldBe emptyList()
            }
        }
    }
})
