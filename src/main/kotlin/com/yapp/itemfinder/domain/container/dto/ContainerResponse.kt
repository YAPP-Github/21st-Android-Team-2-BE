package com.yapp.itemfinder.domain.container.dto

import com.yapp.itemfinder.domain.container.ContainerEntity

data class ContainerResponse(
    val id: Long,
    val icon: String,
    val spaceId: Long,
    val name: String,
    val defaultItemType: String,
    val description: String? = null,
    val imageUrl: String? = null
) {
    constructor(containerEntity: ContainerEntity) : this(
        containerEntity.id,
        containerEntity.iconType.name,
        containerEntity.space.id,
        containerEntity.name,
        containerEntity.defaultItemType.name,
        containerEntity.imageUrl
    )
}
