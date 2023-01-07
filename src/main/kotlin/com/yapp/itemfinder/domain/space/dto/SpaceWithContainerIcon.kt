package com.yapp.itemfinder.domain.space.dto

data class SpaceWithContainerIcon(
    val spaceId: Long,
    val spaceName: String,
    val containerCount: Long,
    val containerIcons: List<String>
)
