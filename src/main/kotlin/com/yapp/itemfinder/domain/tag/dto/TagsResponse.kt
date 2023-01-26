package com.yapp.itemfinder.domain.tag.dto

import com.yapp.itemfinder.domain.tag.TagEntity

data class TagsResponse(
    val tags: List<TagResponse>
) {
    companion object {
        fun from(tags: List<TagEntity>): TagsResponse = TagsResponse(tags.map { TagResponse(it) })
    }
}
