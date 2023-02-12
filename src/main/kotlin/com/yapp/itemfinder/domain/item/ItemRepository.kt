package com.yapp.itemfinder.domain.item

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.common.Const.KST_ZONE_ID
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.QContainerEntity.containerEntity
import com.yapp.itemfinder.domain.item.QItemEntity.itemEntity
import com.yapp.itemfinder.domain.item.dto.ItemDueDateTarget
import com.yapp.itemfinder.domain.item.dto.ItemDueDateTarget.PASSED
import com.yapp.itemfinder.domain.item.dto.ItemDueDateTarget.REMAINED
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.space.QSpaceEntity.spaceEntity
import com.yapp.itemfinder.domain.tag.QItemTagEntity.itemTagEntity
import com.yapp.itemfinder.domain.tag.QTagEntity.tagEntity
import com.yapp.itemfinder.support.PaginationHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface ItemRepository : JpaRepository<ItemEntity, Long>, ItemRepositorySupport {
    @Query(
        "select i from ItemEntity i " +
            "join fetch i.container c " +
            "join fetch c.space " +
            "where i.id = :id"
    )
    fun findByIdWithContainerAndSpace(id: Long): ItemEntity?

    fun deleteAllByContainer(container: ContainerEntity)
}

fun ItemRepository.findByIdWithContainerAndSpaceOrThrowException(id: Long): ItemEntity {
    return findByIdWithContainerAndSpace(id) ?: throw NotFoundException(message = "존재하지 않는 물건입니다")
}

interface ItemRepositorySupport {
    fun search(searchOption: ItemSearchOption, pageable: Pageable, targetContainerIds: List<Long>): Page<ItemEntity>
    fun searchByDueDate(pageable: Pageable, memberId: Long, dueDateTarget: ItemDueDateTarget): Page<ItemEntity>
}

class ItemRepositorySupportImpl(
    private val queryFactory: JPAQueryFactory,
    private val paginationHelper: PaginationHelper
) : ItemRepositorySupport {
    override fun search(searchOption: ItemSearchOption, pageable: Pageable, targetContainerIds: List<Long>): Page<ItemEntity> {
        val query = queryFactory.select(itemEntity)
            .from(itemEntity)
            .leftJoin(itemEntity.tags, itemTagEntity)
            .leftJoin(itemTagEntity.tag, tagEntity)
            .where(
                containerIdIsIn(targetContainerIds),
                itemTypeIsIn(searchOption.itemTypes),
                tagNameIsIn(searchOption.tagNames),
                itemNameContains(searchOption.itemName)
            )
            .groupBy(itemEntity.id)
            .having(tagEntity.count().goe(searchOption.tagNames.size))

        return paginationHelper.getPage(pageable, query, ItemEntity::class.java)
    }

    override fun searchByDueDate(pageable: Pageable, memberId: Long, dueDateTarget: ItemDueDateTarget): Page<ItemEntity> {
        val query = queryFactory.select(itemEntity)
            .from(itemEntity)
            .innerJoin(containerEntity).on(itemEntity.container.id.eq(containerEntity.id))
            .innerJoin(spaceEntity).on(containerEntity.space.id.eq(spaceEntity.id))
            .where(
                spaceEntity.member.id.eq(memberId),
                dueDateConditionOnTarget(dueDateTarget)
            )
            .orderBy(itemEntity.dueDate.asc())
        return paginationHelper.getPage(pageable, query, ItemEntity::class.java)
    }

    private fun dueDateConditionOnTarget(dueDateTarget: ItemDueDateTarget): BooleanExpression {
        val today = LocalDate.now(KST_ZONE_ID)
        val targetDateTime = LocalDateTime.of(today, LocalTime.MIN)

        return when (dueDateTarget) {
            REMAINED -> itemEntity.dueDate.goe(targetDateTime)
            PASSED -> itemEntity.dueDate.lt(targetDateTime)
        }
    }

    private fun containerIdIsIn(targetContainerIds: List<Long>): BooleanExpression {
        return itemEntity.container.id.`in`(targetContainerIds)
    }

    private fun itemNameContains(itemName: String?): BooleanExpression? {
        return if (!itemName.isNullOrBlank()) {
            itemEntity.name.contains(itemName)
        } else {
            null
        }
    }

    private fun itemTypeIsIn(itemTypes: List<ItemType>): BooleanExpression? {
        return if (itemTypes.isNotEmpty()) {
            itemEntity.type.`in`(itemTypes)
        } else {
            null
        }
    }

    private fun tagNameIsIn(tagNames: List<String>): BooleanExpression? {
        return if (tagNames.isNotEmpty()) {
            tagEntity.name.`in`(tagNames)
        } else {
            null
        }
    }
}
