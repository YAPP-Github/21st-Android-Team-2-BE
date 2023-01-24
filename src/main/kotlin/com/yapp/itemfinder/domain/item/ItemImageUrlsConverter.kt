package com.yapp.itemfinder.domain.item

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ItemImageUrlsConverter : AttributeConverter<List<String>, String> {
    private val splitChar = ","

    override fun convertToDatabaseColumn(attribute: List<String>): String {
        return attribute.joinToString(splitChar)
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(splitChar)?.toMutableList()
            ?: listOf()
    }
}
