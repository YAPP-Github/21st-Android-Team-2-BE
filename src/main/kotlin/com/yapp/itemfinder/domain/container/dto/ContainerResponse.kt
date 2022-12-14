package com.yapp.itemfinder.domain.container.dto

import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.service.ContainerVo

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
        id = containerEntity.id,
        icon = containerEntity.iconType.name,
        spaceId = containerEntity.space.id,
        name = containerEntity.name,
        defaultItemType = containerEntity.defaultItemType.name,
        description = containerEntity.description,
        imageUrl = containerEntity.imageUrl
    )

    constructor(containerVo: ContainerVo) : this(
        id = containerVo.id,
        icon = containerVo.iconType,
        spaceId = containerVo.spaceId,
        name = containerVo.name,
        defaultItemType = containerVo.defaultItemType,
        description = containerVo.description,
        imageUrl = containerVo.imageUrl
    )
}
