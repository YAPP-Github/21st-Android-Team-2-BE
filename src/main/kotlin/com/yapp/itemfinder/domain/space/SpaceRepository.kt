package com.yapp.itemfinder.domain.space

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.itemfinder.domain.container.QContainerEntity.containerEntity
import com.yapp.itemfinder.domain.space.QSpaceEntity.spaceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpaceRepository : JpaRepository<SpaceEntity, Long>, SpaceRepositorySupport {
    fun findByMemberIdAndName(memberId: Long, name: String): SpaceEntity?
    fun findByMemberId(memberId: Long): List<SpaceEntity>
    @Query("select s from SpaceEntity s where s.id = :id and s.member.id = :memberId")
    fun findByIdAndMemberId(id: Long, memberId: Long): SpaceEntity?
}

interface SpaceRepositorySupport {
    fun getSpaceWithContainerCountByMemberId(memberId: Long): List<SpaceWithContainerCount>
}

class SpaceRepositorySupportImpl(
    private val queryFactory: JPAQueryFactory
) : SpaceRepositorySupport {
    override fun getSpaceWithContainerCountByMemberId(memberId: Long): List<SpaceWithContainerCount> {
        return queryFactory.select(
            Projections.constructor(
                SpaceWithContainerCount::class.java,
                spaceEntity.id,
                spaceEntity.name,
                containerEntity.id.count()
            )
        )
            .from(spaceEntity)
            .innerJoin(containerEntity).on(containerEntity.space.id.eq(spaceEntity.id))
            .where(spaceEntity.member.id.eq(memberId))
            .groupBy(spaceEntity.id)
            .orderBy(spaceEntity.createdAt.asc())
            .fetch()
    }
}

data class SpaceWithContainerCount(
    val spaceId: Long,
    val spaceName: String,
    val containerCount: Long
)
