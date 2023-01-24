package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.domain.BaseEntity
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.tag.ItemTagEntity
import org.hibernate.annotations.DynamicUpdate
import scala.reflect.internal.util.Statistics.Quantity
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(
    name = "item",
    indexes = [
        Index(name = "idx_container_id", columnList = "container_id")
    ]
)
@Entity
@DynamicUpdate
class ItemEntity(
    id: Long = 0L,
    container: ContainerEntity,
    name: String,
    type: ItemType,
    quantity: Int,
    dueDate: LocalDateTime? = null,
    purchaseDate: LocalDate? = null,
    description: String? = null,
    imageUrls: MutableList<String> = mutableListOf(),
    itemPin: ItemPin? = null
) : BaseEntity(id) {

    @Column(length = 30, nullable = false)
    var name: String = name
        protected set

    @Column(nullable = false)
    var type: ItemType = type
        protected set

    @Column(nullable = false)
    var quantity: Int = quantity
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    var container: ContainerEntity = container
        protected set

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "positionX", column = Column(name = "pin_position_x")),
        AttributeOverride(name = "positionY", column = Column(name = "pin_position_y"))
    )
    var itemPin: ItemPin? = itemPin
        protected set

    @Convert(converter = ItemImageUrlsConverter::class)
    @Column(name = "url")
    var imageUrls: MutableList<String> = imageUrls
        protected set

    @OneToMany(mappedBy = "item", orphanRemoval = true)
    var tags: MutableList<ItemTagEntity> = mutableListOf()

    @Column(length = 200)
    var description: String? = description
        protected set

    var dueDate: LocalDateTime? = dueDate
        protected set

    var purchaseDate: LocalDate? = purchaseDate
        protected set

    fun updateTags(tags: List<ItemTagEntity>) {
        this.tags = tags.toMutableList()
    }
}
