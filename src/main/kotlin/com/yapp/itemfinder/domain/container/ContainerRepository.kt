package com.yapp.itemfinder.domain.container

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContainerRepository : JpaRepository<ContainerEntity, Long> {
    @Query("select new com.yapp.itemfinder.domain.container.SpaceIdToContainerIcon(c.space.id as spaceId, c.iconType) from ContainerEntity c where c.space.id in :spaceIds order by c.createdAt asc")
    fun findIconTypeBySpaceIdIsIn(spaceIds: List<Long>): List<SpaceIdToContainerIcon>
}

data class SpaceIdToContainerIcon(
    val spaceId: Long,
    val iconType: IconType
)
