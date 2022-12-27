package com.yapp.itemfinder.domain.entity.item

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = FoodDetailTemplate::class, name = "FOOD"),
    JsonSubTypes.Type(value = ClothDetailTemplate::class, name = "CLOTH"),
    JsonSubTypes.Type(value = LifeStyleDetailTemplate::class, name = "LIFESTYLE"),
)
@JsonInclude(JsonInclude.Include.NON_NULL)
interface ItemDetailTemplate {
    val type: ItemType
}

class FoodDetailTemplate(
    val price: Int? = null,
    val count: Int? = null,
    val receipt: String? = null,
    override val type: ItemType = ItemType.FOOD
) : ItemDetailTemplate

class ClothDetailTemplate(
    val washingMethod: String? = null,
    val price: Int? = null,
    val brand: String? = null,
    val count: Int? = null,
    val color: String? = null,
    val season: ClothSeason? = null,
    val purpose: String? = null,
    override val type: ItemType = ItemType.CLOTH
) : ItemDetailTemplate {
    enum class ClothSeason {
        SPRING, SUMMER, AUTUMN, WINTER,
    }
}

class LifeStyleDetailTemplate(
    val brand: String? = null,
    override val type: ItemType = ItemType.LIFESTYLE
) : ItemDetailTemplate

@Converter
class ItemDetailTemplateConverter : AttributeConverter<ItemDetailTemplate, String> {
    private val mapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: ItemDetailTemplate): String {
        return try {
            mapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun convertToEntityAttribute(dbData: String): ItemDetailTemplate {
        return try {
            mapper.readValue(dbData, ItemDetailTemplate::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
