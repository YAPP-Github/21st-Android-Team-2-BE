package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ItemTagRepository : JpaRepository<ItemTagEntity, Long> {
    @Query(
        "select new com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeDto(it.tag.id, it.item.type, count(it.item.type)) " +
            "from ItemTagEntity it " +
            "join it.item " +
            "where it.tag in :tags " +
            "group by it.tag, it.item.type"
    )
    fun findByTagIsInGroupByItemType(tags: List<TagEntity>): List<TagWithItemTypeDto>
}
