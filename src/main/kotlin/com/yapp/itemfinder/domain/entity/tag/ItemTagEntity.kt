package com.yapp.itemfinder.domain.entity.tag

import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.item.ItemEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "item_tag")
@Entity
class ItemTagEntity(
    id: Long = 0L,
    item: ItemEntity,
    tag: TagEntity
): BaseEntity(id) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    var item: ItemEntity = item
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    var tag: TagEntity = tag
        protected set
}
