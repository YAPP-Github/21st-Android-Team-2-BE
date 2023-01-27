package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagResponse
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository
) {
    @Transactional
    fun findOrCreateTags(member: MemberEntity, request: CreateTagsRequest): TagsResponse {
        val tagNames = request.tags.map { it.name }.distinct()
        val existingTags = findExistingTags(member, tagNames)
        val newTags = createTags(member, tagNames.filterNot { existingTags.map { t -> t.name }.contains(it) })
        return TagsResponse((existingTags + newTags).map { TagResponse(it) })
    }

    private fun findExistingTags(member: MemberEntity, tagNames: List<String>): List<TagEntity> {
        return tagRepository.findByNameIsInAndMember(tagNames, member)
    }

    private fun createTags(member: MemberEntity, tagNames: List<String>): List<TagEntity> {
        return tagNames.map { tagRepository.save(TagEntity(name = it, member = member)) }
    }
}
