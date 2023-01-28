package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.FakeEntity.createFakeContainerEntity
import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.FakeEntity.createFakeMemberEntity
import com.yapp.itemfinder.FakeEntity.createFakeSpaceEntity
import com.yapp.itemfinder.FakeEntity.createFakeTagEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.ItemRepository
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ItemTagRepositoryTest(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val containerRepository: ContainerRepository,
    private val itemTagRepository: ItemTagRepository,
    private val itemRepository: ItemRepository,
    private val tagRepository: TagRepository
) : BehaviorSpec({

    Given("회원이 아이템에 태그를 등록한 경우") {
        val givenMember = memberRepository.save(createFakeMemberEntity())
        val givenSpace = spaceRepository.save(createFakeSpaceEntity(member = givenMember))
        val givenContainer = containerRepository.save(createFakeContainerEntity(space = givenSpace))
        val givenTag = tagRepository.save(createFakeTagEntity(member = givenMember))

        val fashionCnt = 0
        val lifeCnt = 2
        val foodCnt = 3
        repeat(lifeCnt) {
            val item = itemRepository.save(createFakeItemEntity(type = ItemType.LIFE, container = givenContainer))
            itemTagRepository.save(ItemTagEntity(item = item, tag = givenTag))
        }
        repeat(foodCnt) {
            val item = itemRepository.save(createFakeItemEntity(type = ItemType.FOOD, container = givenContainer))
            itemTagRepository.save(ItemTagEntity(item = item, tag = givenTag))
        }

        When("태그 정보와 해당 태그가 달린 아이템 타입별 개수를 조회하면") {
            val tagsWithItemType = itemTagRepository.findByTagIsInGroupByItemType(listOf(givenTag))

            Then("타입별 개수가 조회된다") {
                for (dto in tagsWithItemType) {
                    dto.tagId shouldBe givenTag.id
                    when (dto.type) {
                        ItemType.FASHION.name -> dto.count shouldBe fashionCnt
                        ItemType.LIFE.name -> dto.count shouldBe lifeCnt
                        ItemType.FOOD.name -> dto.count shouldBe foodCnt
                    }
                }
            }
        }
    }
})
