package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.domain.container.ContainerEntity

data class ContainerVo(
    val id: Long,
    val iconType: String,
    val spaceId: Long,
    val name: String,
    val defaultItemType: String,
    val description: String? = null,
    val imageUrl: String? = null
) {
    constructor(containerEntity: ContainerEntity) : this(
        id = containerEntity.id,
        iconType = containerEntity.iconType.name,
        spaceId = containerEntity.space.id,
        name = containerEntity.name,
        defaultItemType = containerEntity.defaultItemType.name,
        description = containerEntity.description,
        imageUrl = containerEntity.imageUrl
    )
}
