package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.common.DateTimeFormatter.YYYYMMDD
import com.yapp.itemfinder.domain.item.ItemEntity

data class ItemOverviewResponse(
    val tags: List<String>,
    val id: Long,
    val name: String,
    val quantity: Int,
    val itemType: String,
    val useByDate: String? = null,
    val representativeImageUrl: String? = null,
    val pinX: Float? = null,
    val pinY: Float? = null,
    val spaceName: String,
    val containerName: String,
) {
    constructor(item: ItemEntity, tagNames: List<String>) : this(
        tags = tagNames,
        id = item.id,
        name = item.name,
        quantity = item.quantity,
        itemType = item.type.name,
        useByDate = item.dueDate?.format(YYYYMMDD),
        representativeImageUrl = item.imageUrls.firstOrNull(),
        pinX = item.itemPin?.positionX,
        pinY = item.itemPin?.positionY,
        spaceName = item.container.space.name,
        containerName = item.container.name,
    )
}
