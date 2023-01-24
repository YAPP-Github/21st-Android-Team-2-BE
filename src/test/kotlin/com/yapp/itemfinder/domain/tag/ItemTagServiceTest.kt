package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity
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
})
