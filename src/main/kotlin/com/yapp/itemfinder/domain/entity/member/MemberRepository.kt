package com.yapp.itemfinder.domain.entity.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, Long>
