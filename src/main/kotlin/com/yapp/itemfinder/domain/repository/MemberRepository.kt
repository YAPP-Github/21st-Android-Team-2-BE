package com.yapp.itemfinder.domain.repository

import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.Social
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    @Query("select m from MemberEntity m where m.social = :social and m.status = 'ACTIVE'")
    fun findBySocial(social: Social): MemberEntity?

    @Query("select m from MemberEntity m where m.id = :memberId and m.status = 'ACTIVE'")
    fun findActiveMemberById(memberId: Long): MemberEntity?
}
