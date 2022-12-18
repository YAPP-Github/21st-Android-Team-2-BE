package com.yapp.itemfinder.domain.entity.item

import com.yapp.itemfinder.domain.entity.BaseEntity
import com.yapp.itemfinder.domain.entity.ContainerEntity
import com.yapp.itemfinder.domain.entity.tag.ItemTagEntity
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@Table(name = "item")
@Entity
@DynamicUpdate
class ItemEntity(
    id: Long = 0L,
    container: ContainerEntity,
    detailTemplate: ItemDetailTemplate,
    name: String,
    type: ItemType,
    dueDate: LocalDateTime? = null,
    description: String? = null,
    imageUrls: MutableList<String> = mutableListOf()
) : BaseEntity(id) {
    @Convert(converter = ItemDetailTemplateConverter::class)
    var detailTemplate: ItemDetailTemplate = detailTemplate
        protected set

    @Column(length = 30, nullable = false)
    var name: String = name
        protected set

    @Column(nullable = false)
    var type: ItemType = type
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    var container: ContainerEntity = container
        protected set

    @OneToOne(mappedBy = "item", orphanRemoval = true)
    var pin: ItemPinEntity? = null
        protected set

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_image", joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "url")
    var imageUrls: MutableList<String> = imageUrls
        protected set

    @OneToMany(mappedBy = "item", orphanRemoval = true)
    var tags: MutableList<ItemTagEntity> = mutableListOf()

    @Column(length = 255)
    var description: String? = description
        protected set

    var dueDate: LocalDateTime? = dueDate
        protected set

    fun updateTags(tags: List<ItemTagEntity>) {
        this.tags = tags.toMutableList()
    }

    fun updatePin(pin: ItemPinEntity) {
        this.pin = pin
    }
}
