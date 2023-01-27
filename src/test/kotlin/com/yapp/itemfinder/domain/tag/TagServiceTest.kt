package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.domain.tag.dto.CreateTagRequest
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class TagServiceTest : BehaviorSpec({
    val tagRepository = mockk<TagRepository>()
    val tagService = TagService(tagRepository)

    Given("회원이 태그를 이미 등록한 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenTag = FakeEntity.createFakeTagEntity(member = givenMember)

        every { tagRepository.findByNameIsInAndMember(any(), givenMember) } returns listOf(givenTag)

        When("태그를 등록하면") {
            val request = CreateTagsRequest(listOf(CreateTagRequest(givenTag.name)))
            val createTag = tagService.findOrCreateTags(givenMember, request)

            Then("등록된 태그 정보를 조회하여 반환한다") {
                createTag.tags.size shouldBe 1
                createTag.tags[0].id shouldBe givenTag.id
                createTag.tags[0].name shouldBe givenTag.name
            }
        }
    }

    Given("회원이 태그를 등록하지 않은 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()

        every { tagRepository.findByNameIsInAndMember(any(), givenMember) } returns emptyList()
        every { tagRepository.save(any()) } answers { firstArg() }

        When("태그를 등록하면") {
            val names = listOf("새 태그1", "새 태그2")
            val request = CreateTagsRequest(names.map { CreateTagRequest(it) })
            val createTag = tagService.findOrCreateTags(givenMember, request)

            Then("새로운 태그를 추가하고 정보를 반환한다") {
                createTag.tags.size shouldBe names.size
                createTag.tags.map { it.name } shouldContainInOrder (names)
            }
        }
    }
})
