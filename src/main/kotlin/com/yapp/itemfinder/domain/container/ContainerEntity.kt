package com.yapp.itemfinder.domain.container

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.BaseEntity
import com.yapp.itemfinder.domain.space.SpaceEntity
import javax.persistence.AttributeConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "container",
    indexes = [
        Index(name = "idx_space_id", columnList = "space_id")
    ]
)
class ContainerEntity(
    space: SpaceEntity,
    name: String = DEFAULT_CONTAINER_NAME,
    iconType: IconType = IconType.IC_CONTAINER_1,
    imageUrl: String? = null,
    id: Long = 0L
) : BaseEntity(id) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    var space: SpaceEntity = space
        protected set

    @Column(length = CONTAINER_NAME_LENGTH_LIMIT, nullable = false)
    var name: String = name
        protected set

    @Convert(converter = IconTypeConverter::class)
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    var iconType: IconType = iconType
        protected set

    var imageUrl: String? = imageUrl
        protected set

    fun getCreatorId(): Long {
        return space.getCreatorId()
    }

    fun update(space: SpaceEntity, name: String, iconType: String, imageUrl: String?): ContainerEntity {
        this.space = space
        this.name = name
        this.iconType = IconType.values().firstOrNull { it.name == iconType }
            ?: throw BadRequestException(message = "올바르지 않은 아이콘으로 요청하셨습니다.")
        this.imageUrl = imageUrl
        return this
    }

    companion object {
        const val DEFAULT_CONTAINER_NAME = "보관함"
        const val CONTAINER_NAME_LENGTH_LIMIT = 15
    }
}

enum class IconType(val value: Int) {
    IC_CONTAINER_1(1),
    IC_CONTAINER_2(2),
    IC_CONTAINER_3(3),
    IC_CONTAINER_4(4),
    IC_CONTAINER_5(5),
    IC_CONTAINER_6(6),
    IC_CONTAINER_7(7),
    IC_CONTAINER_8(8)
}

class IconTypeConverter : AttributeConverter<IconType, Int> {
    override fun convertToDatabaseColumn(attribute: IconType?): Int {
        return attribute?.value ?: IconType.IC_CONTAINER_1.value
    }

    override fun convertToEntityAttribute(dbData: Int): IconType {
        return IconType.values().firstOrNull { it.value == dbData } ?: IconType.IC_CONTAINER_1
    }
}
