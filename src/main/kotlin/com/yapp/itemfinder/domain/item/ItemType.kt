package com.yapp.itemfinder.domain.item

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class ItemType(val value: String) {
    LIFE("생활"),
    FOOD("식품"),
    FASHION("패션"),
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
