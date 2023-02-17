package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.domain.item.ItemEntity
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface ItemTagRepository : JpaRepository<ItemTagEntity, Long> {
    @Query(
        "select new com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto(it.tag.id, it.item.type, count(it.item.type)) " +
            "from ItemTagEntity it " +
            "join it.item " +
            "where it.tag in :tags " +
            "group by it.tag, it.item.type"
    )
    fun findByTagIsInGroupByItemType(tags: List<TagEntity>): List<TagWithItemTypeDto>

    @Query(
        value = "select new com.yapp.itemfinder.domain.tag.ItemIdWithTagName(itemTag.item.id, tag.name) " +
            "from ItemTagEntity itemTag inner join TagEntity tag on itemTag.tag = tag where itemTag.item.id in :itemIds"
    )
    fun findItemIdAndTagNameByItemIdIsIn(itemIds: List<Long>): List<ItemIdWithTagName>

    @Modifying
    @Transactional
    @Query("delete from ItemTagEntity i where i.item in :items")
    fun deleteAllByItemIsIn(items: List<ItemEntity>)
}

data class ItemIdWithTagName(
    val itemId: Long,
    val tagName: String
)
