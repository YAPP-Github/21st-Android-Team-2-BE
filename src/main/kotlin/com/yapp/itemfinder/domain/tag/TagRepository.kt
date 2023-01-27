package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.domain.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<TagEntity, Long> {
    @Query("select t from TagEntity t where t.id in :ids and t.member.id = :memberId")
    fun findByIdIsInAndMemberId(ids: List<Long>, memberId: Long): List<TagEntity>
    fun findByNameIsInAndMember(name: List<String>, member: MemberEntity): List<TagEntity>
}
