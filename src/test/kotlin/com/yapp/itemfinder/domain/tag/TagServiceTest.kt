package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class TagServiceTest : BehaviorSpec({
    val tagRepository = mockk<TagRepository>()
    val itemTagRepository = mockk<ItemTagRepository>()
    val tagService = TagService(tagRepository, itemTagRepository)

    Given("회원이 태그를 등록한 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenTags: MutableList<TagEntity> = mutableListOf()
        repeat(3) {
            givenTags.add(FakeEntity.createFakeTagEntity(member = givenMember))
        }

        every { tagRepository.findByNameIsInAndMember(any(), givenMember) } returns givenTags
        every { tagRepository.findByMember(givenMember) } returns givenTags

        When("태그를 등록하면") {
            val request = CreateTagsRequest(givenTags.map { it.name })
            val createTags = tagService.findOrCreateTags(givenMember, request)

            Then("등록된 태그 정보를 조회하여 반환한다") {
                createTags.tags.size shouldBe 3
                createTags.tags.map { it.id }.containsAll(givenTags.map { it.id }) shouldBe true
            }
        }

        When("전체 태그를 조회하면") {
            val tags = tagService.findTags(givenMember).tags

            Then("회원의 모든 태그를 반환한다") {
                tags.size shouldBe givenTags.size
                tags.map { it.id } shouldContainAll givenTags.map { it.id }
            }
        }

        And("태그를 아이템에 등록한 경우") {
            val givenItem = FakeEntity.createFakeItemEntity(type = ItemType.FASHION)
            val pageable = PageRequest.of(0, 10)
            val latestTags = givenTags.reversed()
            every { tagRepository.findByMemberOrderByCreatedAtDesc(givenMember, pageable) } returns PageImpl(latestTags, pageable, givenTags.size.toLong())
            every { itemTagRepository.findByTagIsInGroupByItemType(latestTags) } returns latestTags.map { TagWithItemTypeDto(it.id, givenItem.type, 1) }

            When("가장 최신 태그 10개를 상세 조회하면") {
                val tagPage = tagService.findTagWithItemType(givenMember, pageable)

                Then("회원의 모든 태그를 최신순으로 반환한다") {
                    tagPage.totalPages shouldBe 1
                    tagPage.totalCount shouldBe givenTags.size
                    tagPage.currentPageNumber shouldBe 0
                    tagPage.hasNext shouldBe false
                    tagPage.data.size shouldBe givenTags.size
                    tagPage.data.map { it.id } shouldContainInOrder givenTags.reversed().map { it.id }
                    tagPage.data.map { it.itemType.size shouldBe ItemType.values().size }
                    tagPage.data.map {
                        it.itemType.map { typeCount ->
                            when (typeCount.type) {
                                givenItem.type.name -> typeCount.count shouldBe 1
                                else -> typeCount.count shouldBe 0
                            }
                        }
                    }
                }
            }
        }
    }

    Given("회원이 태그를 등록하지 않은 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()

        every { tagRepository.findByNameIsInAndMember(any(), givenMember) } returns emptyList()
        every { tagRepository.save(any()) } answers { firstArg() }

        When("태그를 등록하면") {
            val names = listOf("새 태그1", "새 태그2")
            val request = CreateTagsRequest(names)
            val createTag = tagService.findOrCreateTags(givenMember, request)

            Then("새로운 태그를 추가하고 정보를 반환한다") {
                createTag.tags.size shouldBe names.size
                createTag.tags.map { it.name } shouldContainInOrder (names)
            }
        }
    }
})
