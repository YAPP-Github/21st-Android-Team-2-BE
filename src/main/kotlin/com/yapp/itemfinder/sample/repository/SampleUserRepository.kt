package com.yapp.itemfinder.sample.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.itemfinder.sample.entity.SampleUser
import org.springframework.data.jpa.repository.JpaRepository

interface SampleUserRepository : JpaRepository<SampleUser, Long>, SampleUserRepositoryCustom {
    fun existsByEmail(email: String): Boolean
}

interface SampleUserRepositoryCustom {
}

class SampleUserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : SampleUserRepositoryCustom {
}
