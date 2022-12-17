package com.yapp.itemfinder.domain.entity

import javax.persistence.*

@Entity
@Table(name = "container")
class ContainerEntity(
    space: SpaceEntity,
    name: String,
    defaultItemType: String,
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

    @Column(length = 20, nullable = false)
    var defaultItemType: String = defaultItemType
        protected set

    var description: String = description
        protected set

    var imageUrl: String = imageUrl
        protected set
}
