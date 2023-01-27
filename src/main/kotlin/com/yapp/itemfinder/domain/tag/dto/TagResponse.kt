package com.yapp.itemfinder.domain.tag.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.yapp.itemfinder.domain.tag.TagEntity

data class TagResponse(
    val id: Long,
    val name: String
) {
    constructor(tagEntity: TagEntity) : this(
        id = tagEntity.id,
        name = tagEntity.name
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TagsResponse(
    val tags: List<TagResponse>
) {
    companion object {
        fun from(tags: List<TagEntity>): TagsResponse = TagsResponse(tags.map { TagResponse(it) })
    }
}
