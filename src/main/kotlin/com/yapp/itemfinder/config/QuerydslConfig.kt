package com.yapp.itemfinder.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class QuerydslConfig {

    @Bean
    fun queryFactory(entityManager: EntityManager) = JPAQueryFactory(entityManager)
}