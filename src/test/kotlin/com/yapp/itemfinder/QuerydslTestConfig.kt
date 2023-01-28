package com.yapp.itemfinder

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.ItemRepository
import com.yapp.itemfinder.domain.member.MemberRepository
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.support.PaginationHelper
import com.yapp.itemfinder.domain.tag.ItemTagRepository
import com.yapp.itemfinder.domain.tag.TagRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TestConfiguration
class QuerydslTestConfig {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
    @Bean
    fun paginationHelper(): PaginationHelper {
        return PaginationHelper(entityManager)
    }
    @Bean
    fun testCaseUtil(
        itemRepository: ItemRepository,
        memberRepository: MemberRepository,
        containerRepository: ContainerRepository,
        spaceRepository: SpaceRepository,
        itemTagRepository: ItemTagRepository,
        tagRepository: TagRepository
    ): TestCaseUtil {
        return TestCaseUtil(
            itemRepository = itemRepository,
            memberRepository = memberRepository,
            containerRepository = containerRepository,
            spaceRepository = spaceRepository,
            itemTagRepository = itemTagRepository,
            tagRepository = tagRepository
        )
    }
}
