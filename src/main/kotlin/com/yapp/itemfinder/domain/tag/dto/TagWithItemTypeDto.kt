package com.yapp.itemfinder.domain.tag.dto

import com.yapp.itemfinder.domain.item.ItemType

data class TagWithItemTypeDto(
    val tagId: Long,
    val type: String,
    val count: Long
) {
    constructor(tagId: Long, type: ItemType, count: Long) : this(tagId, type.name, count)
}
