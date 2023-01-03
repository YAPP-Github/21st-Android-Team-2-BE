package com.yapp.itemfinder.domain.space

import org.springframework.data.jpa.repository.JpaRepository

interface SpaceRepository : JpaRepository<SpaceEntity, Long> {
    fun findByMemberIdAndName(memberId: Long, name: String): SpaceEntity?
    fun findByMemberId(memberId: Long): List<SpaceEntity>
}
