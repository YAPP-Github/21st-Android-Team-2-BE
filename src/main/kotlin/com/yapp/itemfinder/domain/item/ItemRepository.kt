package com.yapp.itemfinder.domain.item

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.itemfinder.domain.item.QItemEntity.itemEntity
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.tag.QItemTagEntity.itemTagEntity
import com.yapp.itemfinder.domain.tag.QTagEntity.tagEntity
import com.yapp.itemfinder.support.PaginationHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<ItemEntity, Long>, ItemRepositorySupport
interface ItemRepositorySupport {
    fun search(searchOption: ItemSearchOption, pageable: Pageable, targetContainerIds: List<Long>): Page<ItemEntity>
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

    private fun containerIdIsIn(targetContainerIds: List<Long>): BooleanExpression? {
        return if (targetContainerIds.isNotEmpty()) {
            itemEntity.container.id.`in`(targetContainerIds)
        } else {
            null
        }
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
