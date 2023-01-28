package com.yapp.itemfinder

import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.ItemEntity
import com.yapp.itemfinder.domain.item.ItemRepository
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.tag.ItemTagRepository
import com.yapp.itemfinder.domain.tag.TagRepository

class TestCaseUtil(
    private val itemRepository: ItemRepository,
    private val memberRepository: MemberRepository,
    private val containerRepository: ContainerRepository,
    private val spaceRepository: SpaceRepository,
    private val itemTagRepository: ItemTagRepository,
    private val tagRepository: TagRepository
) {
    fun `한 명의 회원과 해당 회원이 저장한 하나의 아이템 반환`(): Pair<MemberEntity, ItemEntity> {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = givenMember))
        val givenContainer = containerRepository.save(FakeEntity.createFakeContainerEntity(space = givenSpace))
        val givenItem = itemRepository.save(FakeEntity.createFakeItemEntity(container = givenContainer))
        return givenMember to givenItem
    }

    fun `한 명의 회원과 해당 회원이 저장한 보관함 반환`(): Pair<MemberEntity, ContainerEntity> {
        val givenMember = memberRepository.save(FakeEntity.createFakeMemberEntity())
        val givenSpace = spaceRepository.save(FakeEntity.createFakeSpaceEntity(member = givenMember))
        val givenContainer = containerRepository.save(FakeEntity.createFakeContainerEntity(space = givenSpace))
        return givenMember to givenContainer
    }

    fun `한 개의 아이템에 전달받은 태그 이름들에 대한 태그 등록`(
        item: ItemEntity,
        tagNames: List<String>,
        member: MemberEntity
    ) {
        tagNames.forEach {
            val tag = tagRepository.save(FakeEntity.createFakeTagEntity(member = member, name = it))
            itemTagRepository.save(FakeEntity.createFakeItemTagEntity(item = item, tag = tag))
        }
    }
}
