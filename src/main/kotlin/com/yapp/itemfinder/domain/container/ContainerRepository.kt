package com.yapp.itemfinder.domain.container

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContainerRepository : JpaRepository<ContainerEntity, Long> {
    @Query("select c from ContainerEntity c where c.space.id in :spaceIds order by c.createdAt asc")
    fun findBySpaceIdIsIn(spaceIds: List<Long>): List<ContainerEntity>
}
