package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.api.PageResponse
import com.yapp.itemfinder.domain.item.ItemType
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeResponse
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import com.yapp.itemfinder.domain.tag.dto.TypeCount
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository,
    private val itemTagRepository: ItemTagRepository
) {
    @Transactional
    fun findOrCreateTags(member: MemberEntity, request: CreateTagsRequest): TagsResponse {
        val tagNames = request.tags.distinct()
        val existingTags = findExistingTags(member, tagNames)
        val newTags = createTags(member, tagNames.filterNot { existingTags.map(TagEntity::name).contains(it) })
        return TagsResponse.from(existingTags + newTags)
    }

    private fun findExistingTags(member: MemberEntity, tagNames: List<String>): List<TagEntity> {
        return tagRepository.findByNameIsInAndMember(tagNames, member)
    }

    private fun createTags(member: MemberEntity, tagNames: List<String>): List<TagEntity> {
        return tagNames.map { tagRepository.save(TagEntity(name = it, member = member)) }
    }

    fun findTags(member: MemberEntity): TagsResponse {
        val tags = tagRepository.findByMemberOrderByCreatedAtDesc(member)
        return TagsResponse.from(tags)
    }

    fun findTagWithItemType(member: MemberEntity, pageable: Pageable): PageResponse<TagWithItemTypeResponse> {
        val tagPage = tagRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
        val tagWithItemType = itemTagRepository.findByTagIsInGroupByItemType(tagPage.content)
            .groupBy(TagWithItemTypeDto::tagId) // key:tagId, value:type,count

        val tags = tagPage.content.map {
            val itemType = ItemType.values().associate { type -> type.name to 0L } as MutableMap
            if (tagWithItemType.contains(it.id)) {
                tagWithItemType[it.id].orEmpty().map { t -> itemType.put(t.type, t.count) }
            }
            TagWithItemTypeResponse(it.id, it.name, itemType.map { t -> TypeCount(t.key, t.value) })
        }
        return PageResponse(data = tags, totalCount = tagPage.totalElements.toInt(), totalPages = tagPage.totalPages, currentPageNumber = tagPage.number, hasNext = tagPage.hasNext())
    }
}
