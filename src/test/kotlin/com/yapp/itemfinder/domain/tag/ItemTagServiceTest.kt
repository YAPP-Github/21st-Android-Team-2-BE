package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.api.exception.BadRequestException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ItemTagServiceTest : BehaviorSpec({
    val tagRepository = mockk<TagRepository>()
    val itemTagRepository = mockk<ItemTagRepository>()
    val itemTagService = ItemTagService(tagRepository, itemTagRepository)

    Given("회원이 태그를 등록한 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenTags = mutableListOf<TagEntity>()
        repeat(3) {
            givenTags.add(FakeEntity.createFakeTagEntity())
        }
        every { tagRepository.findByIdIsInAndMemberId(givenTags.map { it.id }, givenMember.id) } returns givenTags
        every { itemTagRepository.save(any()) } answers { firstArg() }

        When("회원이 물건에 태그를 추가하면") {
            val item = FakeEntity.createFakeItemEntity()
            val itemTags = itemTagService.createItemTags(item, givenTags.map { it.id }, givenMember.id)

            Then("물건 태그가 생성된다") {
                itemTags.size shouldBe givenTags.size
                itemTags[0].item shouldBe item
            }
        }
    }

    Given("회원이 태그를 등록하지 않은 경우") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenTags = mutableListOf<TagEntity>()
        repeat(3) {
            givenTags.add(FakeEntity.createFakeTagEntity())
        }
        every { tagRepository.findByIdIsInAndMemberId(givenTags.map { it.id }, givenMember.id) } returns listOf()
        every { itemTagRepository.save(any()) } answers { firstArg() }

        When("회원이 물건에 태그를 추가하면") {
            val item = FakeEntity.createFakeItemEntity()

            Then("예외가 발생한다") {
                shouldThrow<BadRequestException> {
                    itemTagService.createItemTags(item, givenTags.map { it.id }, givenMember.id)
                }
            }
        }
    }

    Given("특정 아이템에 등록된 태그들이 존재하는 경우") {
        val givenTagNames = listOf("tag1", "tag2")
        val givenItemIds = listOf(generateRandomPositiveLongValue())
        val givenItemIdWithTagNames: List<ItemIdWithTagName> = listOf(
            ItemIdWithTagName(itemId = givenItemIds[0], givenTagNames[0]),
            ItemIdWithTagName(itemId = givenItemIds[0], givenTagNames[1]),
        )

        every { itemTagRepository.findItemTagNameItemIdIsIn(givenItemIds) } returns givenItemIdWithTagNames

        When("아이템 아이디 리스트로 해당 아이디에 속한 태그 이름을 조회하면") {
            val itemIdToTagNames = itemTagService.createItemIdToTagNames(itemIds = givenItemIds)

            Then("아이템 아이디: key, 해당 아이템에 매핑된 태그 이름들이 value인 맵 형태로 해당 정보를 반환한다") {
                itemIdToTagNames.size shouldBe 1
                itemIdToTagNames[givenItemIds.first()] shouldBe givenTagNames
            }
        }
    }
})
