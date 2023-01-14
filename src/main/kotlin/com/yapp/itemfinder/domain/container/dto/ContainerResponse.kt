package com.yapp.itemfinder.domain.container.dto

import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.service.ContainerVo

data class ContainerResponse(
    val id: Long,
    val icon: String,
    val spaceId: Long,
    val name: String,
    val imageUrl: String? = null
) {
    constructor(containerEntity: ContainerEntity) : this(
        id = containerEntity.id,
        icon = containerEntity.iconType.name,
        spaceId = containerEntity.space.id,
        name = containerEntity.name,
        imageUrl = containerEntity.imageUrl
    )

    constructor(containerVo: ContainerVo) : this(
        id = containerVo.id,
        icon = containerVo.iconType,
        spaceId = containerVo.spaceId,
        name = containerVo.name,
        imageUrl = containerVo.imageUrl
    )
}
