package com.yapp.itemfinder.domain.container

import com.yapp.itemfinder.domain.space.SpaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContainerRepository : JpaRepository<ContainerEntity, Long> {
    @Query("select c from ContainerEntity c where c.space.id in :spaceIds order by c.createdAt asc")
    fun findBySpaceIdIsIn(spaceIds: List<Long>): List<ContainerEntity>

    fun findBySpaceOrderByCreatedAtAsc(space: SpaceEntity): List<ContainerEntity>

    fun findBySpace(space: SpaceEntity): List<ContainerEntity>

    fun findBySpaceIdAndName(spaceId: Long, name: String): ContainerEntity?
    @Query("select c from ContainerEntity c join fetch c.space s where c.id = :id")
    fun findByIdWithSpace(id: Long): ContainerEntity?
    @Query("select c from ContainerEntity c join fetch c.space where c.id = :id and c.space.member.id = :memberId")
    fun findWithSpaceByIdAndMemberId(id: Long, memberId: Long): ContainerEntity?

    @Query("select c from ContainerEntity c join fetch c.space where c.space.member.id = :memberId")
    fun findByMemberId(memberId: Long): List<ContainerEntity>

    fun countBySpace(space: SpaceEntity): Long
}
