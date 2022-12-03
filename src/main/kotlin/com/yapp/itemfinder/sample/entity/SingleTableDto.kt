package com.yapp.itemfinder.sample.entity

import com.fasterxml.jackson.annotation.JsonInclude
import java.lang.IllegalArgumentException

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SingleTableItemDto(
    val name: String,
    val description: String,
    val taste: String?,
    val type: String,
    val gram: Int?,
    val period: String?
) {
    companion object {

        fun of(item: Item): SingleTableItemDto {
            return when (item) {
                is FoodSingleTable -> of(food = item)
                is LifeSingleTable -> of(life = item)
                else -> {
                    throw IllegalArgumentException("오류")
                }
            }
        }

        fun of(food: FoodSingleTable): SingleTableItemDto {
            return SingleTableItemDto(name = food.name, description = food.description, taste = food.taste, gram = food.gram, period = null,
            type = food.type)
        }
        fun of(life: LifeSingleTable): SingleTableItemDto {
            return SingleTableItemDto(name = life.name, description = life.description, period = life.period, taste = null, gram = null, type = life.type)
        }
    }


}
