package com.yapp.itemfinder.domain.tag

import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long> {
    fun findByIdIsInAndMemberId(ids: List<Long>, memberId: Long): List<TagEntity>
}
