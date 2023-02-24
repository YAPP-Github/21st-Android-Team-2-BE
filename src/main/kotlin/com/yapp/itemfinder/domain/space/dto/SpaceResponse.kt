package com.yapp.itemfinder.domain.space.dto

import com.yapp.itemfinder.domain.space.SpaceEntity

data class SpaceResponse(val name: String, val id: Long) {
    constructor(space: SpaceEntity) : this(space.name, space.id)
}

data class SpacesResponse(val spaces: List<SpaceResponse>) {
    companion object {
        fun from(spaces: List<SpaceEntity>): SpacesResponse {
            return SpacesResponse(
                spaces = spaces.map { SpaceResponse(it) }
            )
        }
    }
}
