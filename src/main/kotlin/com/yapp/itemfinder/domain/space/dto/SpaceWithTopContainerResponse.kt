package com.yapp.itemfinder.domain.space.dto

import com.yapp.itemfinder.domain.container.dto.ContainerResponse

data class SpaceWithTopContainerResponse(
    val spaceId: Long,
    val spaceName: String,
    val containerCount: Long,
    val topContainers: List<ContainerResponse>
)
