package com.yapp.itemfinder.domain.tag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<TagEntity, Long> {
    @Query("select t from TagEntity t where t.id in :ids and t.member.id = :memberId")
    fun findByIdIsInAndMemberId(ids: List<Long>, memberId: Long): List<TagEntity>
}
