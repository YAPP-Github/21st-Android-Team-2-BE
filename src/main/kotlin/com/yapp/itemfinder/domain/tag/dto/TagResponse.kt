package com.yapp.itemfinder.domain.tag.dto

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
