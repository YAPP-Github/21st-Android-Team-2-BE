package com.yapp.itemfinder.domain.entity.space.dto

import com.yapp.itemfinder.domain.entity.space.SpaceEntity

data class SpaceResponse(val name: String, val id: Long) {
    companion object {
        fun from(space: SpaceEntity): SpaceResponse {
            return SpaceResponse(
                name = space.name,
                id = space.id
            )
        }
    }
}

data class SpacesResponse(val spaces: List<SpaceResponse>) {
    companion object {
        fun from(spaces: List<SpaceEntity>): SpacesResponse {
            return SpacesResponse(
                spaces = spaces.map { SpaceResponse.from(it) }
            )
        }
    }
}
