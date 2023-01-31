package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.BaseEntity
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.tag.ItemTagEntity
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Convert
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
    imageUrls: List<String> = emptyList(),
    itemPin: ItemPin? = null
) : BaseEntity(id) {
    constructor(
        id: Long = 0L,
        container: ContainerEntity,
        name: String,
        type: ItemType,
        quantity: Int,
        dueDate: LocalDateTime? = null,
        purchaseDate: LocalDate? = null,
        description: String? = null,
        imageUrls: List<String> = emptyList(),
        pinX: Float? = null,
        pinY: Float? = null
    ) : this(
        id = id,
        container = container,
        name = name,
        type = type,
        quantity = quantity,
        dueDate = dueDate,
        purchaseDate = purchaseDate,
        description = description,
        imageUrls = imageUrls,
        itemPin = if (pinX != null && pinY != null) {
            ItemPin(pinX, pinY)
        } else null
    )

    init {
        validateItemPin(itemPin, container)
        validateDueDate(type, dueDate)
    }

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
    var imageUrls: List<String> = imageUrls
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

    private fun validateItemPin(itemPin: ItemPin?, container: ContainerEntity) {
        itemPin?.let {
            requireNotNull(container.imageUrl) {
                throw BadRequestException(message = "핀을 등록할 수 없습니다")
            }
        }
    }

    private fun validateDueDate(itemType: ItemType, dueDate: LocalDateTime?) {
        dueDate?.let {
            require(itemType != ItemType.FASHION) {
                throw BadRequestException(message = "패션 카테고리에는 소비기한을 등록할 수 없습니다")
            }
        }
    }

    fun isValidMemberId(memberId: Long): Boolean {
        return this.container.space.member.id == memberId
    }
}
