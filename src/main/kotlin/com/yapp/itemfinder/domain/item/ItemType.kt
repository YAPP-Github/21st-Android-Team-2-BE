package com.yapp.itemfinder.domain.item

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class ItemType(val value: String) {
    LIFESTYLE("생활용품"),
    FOOD("식품"),
    CLOTH("의류"),
}

@Converter(autoApply = true)
class ItemTypeConverter : AttributeConverter<ItemType, String> {
    override fun convertToDatabaseColumn(attribute: ItemType?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): ItemType? {
        return if (dbData == null) {
            return null
        } else {
            ItemType.values().firstOrNull { it.name == dbData }
        }
    }
}
