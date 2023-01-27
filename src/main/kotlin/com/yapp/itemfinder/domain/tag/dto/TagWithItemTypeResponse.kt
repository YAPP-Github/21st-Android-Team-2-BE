package com.yapp.itemfinder.domain.tag.dto

import io.swagger.v3.oas.annotations.media.Schema

data class TagWithItemTypeResponse(
    val id: Long,
    val name: String,
    @Schema(description = "카테고리별 개수")
    val itemType: List<TypeCount>
)

data class TypeCount(
    val type: String,
    val count: Long
)
