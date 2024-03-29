package com.yapp.itemfinder.domain.item.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.yapp.itemfinder.domain.item.ItemEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class ItemDetailResponse(
    val id: Long,
    val name: String,
    val itemType: String,
    val quantity: Int,
    val imageUrls: List<String>,
    val spaceName: String,
    val containerName: String,
    val tags: List<String>,
    val description: String? = null,
    @JsonFormat(pattern = "yyyy.MM.dd")
    val purchaseDate: LocalDate? = null,
    @JsonFormat(pattern = "yyyy.MM.dd")
    val useByDate: LocalDateTime? = null,
    val containerImageUrl: String? = null,
    val pinX: Float? = null,
    val pinY: Float? = null
) {
    constructor(item: ItemEntity) : this(
        id = item.id,
        name = item.name,
        itemType = item.type.name,
        quantity = item.quantity,
        imageUrls = item.imageUrls,
        spaceName = item.container.space.name,
        containerName = item.container.name,
        tags = item.tags.map { it.tag.name },
        description = item.description,
        purchaseDate = item.purchaseDate,
        useByDate = item.dueDate,
        containerImageUrl = item.container.imageUrl,
        pinX = item.itemPin?.positionX,
        pinY = item.itemPin?.positionY
    )
}
