package com.yapp.itemfinder.domain.entity.item

import com.yapp.itemfinder.domain.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "item_pin")
class ItemPinEntity(
    id: Long = 0L,
    item: ItemEntity,
    positionX: Float,
    positionY: Float,
): BaseEntity(id) {
    @Column(name = "pin_position_x")
    var positionX: Float = positionX
        protected set

    @Column(name = "pin_position_y")
    var positionY: Float = positionY
        protected set

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    var item: ItemEntity = item
        protected set
}
