package com.yapp.itemfinder.domain.container

import com.yapp.itemfinder.domain.space.SpaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContainerRepository : JpaRepository<ContainerEntity, Long> {
    @Query("select c from ContainerEntity c where c.space.id in :spaceIds order by c.createdAt asc")
    fun findBySpaceIdIsIn(spaceIds: List<Long>): List<ContainerEntity>

    fun findBySpaceOrderByCreatedAtAsc(space: SpaceEntity): List<ContainerEntity>
    fun findBySpaceIdAndName(spaceId: Long, name: String): ContainerEntity?
}
