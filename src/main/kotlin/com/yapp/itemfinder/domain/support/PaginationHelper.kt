package com.yapp.itemfinder.domain.support

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilderFactory
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

@Component
class PaginationHelper(
    private val entityManager: EntityManager,
) {
    private fun getQuerydsl(domainClass: Class<*>): Querydsl {
        return Querydsl(entityManager, PathBuilderFactory().create(domainClass))
    }

    fun <T> getPage(pageable: Pageable, query: JPAQuery<T>, domainClass: Class<*>): Page<T> {
        val countQuery = query.clone(entityManager).select(Expressions.ONE) as JPQLQuery<*>

        val content = getQuerydsl(domainClass)
            .applyPagination(pageable, query)
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount)
    }
}
