package com.yapp.itemfinder.domain.entity.container

import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.item.ItemType
import com.yapp.itemfinder.domain.entity.space.SpaceEntity
import org.hibernate.annotations.ColumnDefault
import javax.persistence.*

@Entity
@Table(name = "container",
    indexes = [
        Index(name = "idx_space_id", columnList = "space_id")
    ]
)
class ContainerEntity(
    space: SpaceEntity,
    name: String,
    defaultItemType: ItemType = ItemType.LIFESTYLE,
    description: String,
    imageUrl: String,
    id: Long = 0L
) : BaseEntity(id) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    var space: SpaceEntity = space
        protected set

    @Column(length = 30, nullable = false)
    var name: String = name
        protected set

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @ColumnDefault("'LIFESTYLE'")
    var defaultItemType: ItemType = defaultItemType
        protected set

    var description: String = description
        protected set

    var imageUrl: String = imageUrl
        protected set
}
