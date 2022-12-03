package com.yapp.itemfinder.sample.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.AttributeConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity


// 라이브러리 사용하는 방식
@Entity
@TypeDefs(
    value = [
    TypeDef(name = "json", typeClass = JsonStringType::class)
])
class Items(id: Long = 0L, history: Map<String,Any>) : BaseEntity(id) {

    @Type(type = "json")
    @Column(
        name = "itemInfos",
        columnDefinition = "longtext"
    )
    val itemInfo: Map<String, Any> = history

    companion object {
        fun of(history: Map<String, Any>): Items {
            return Items(0L, history)
        }
    }
}

interface ProductItemRepository: JpaRepository<ProductItem, Long>

// 커스텀하게 사용하는 방식
@Entity
class ProductItem(id: Long = 0L, tests: Template) : BaseEntity(id) {

    @Convert(converter = TemplateConverter::class)
    var template: Template = tests

    companion object {
        fun of(template: Template): ProductItem {
            return ProductItem(0L, template)
        }
    }

    fun updateTemplate(template: Template) {
        this.template = template
    }
}

class TemplateConverter : AttributeConverter<Template, String> {
    override fun convertToDatabaseColumn(attribute: Template): String {
        return try {
            jacksonObjectMapper().writeValueAsString(attribute)
        } catch (e: Exception) {
            println("e =$e")
            throw e
        }
    }

    override fun convertToEntityAttribute(dbData: String): Template {
        val readValue = ObjectMapper().readValue(dbData, Template::class.java)
        return ObjectMapper().readValue(dbData, Template::class.java)
    }
}

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Food::class, name = "FOOD"),
    JsonSubTypes.Type(value = LifeStyle::class, name = "LIFESTYLE"),
)
interface Template {

    val type: TemplateType
}

// 빈 생성자
//@JsonTypeName("FOOD")
data class Food(
    val taste: String = "",
    val gram: Int = 0
) : Template {

    override val type: TemplateType = TemplateType.FOOD
}

//@JsonTypeName("LIFESTYLE")
data class LifeStyle(
    val period: String = "",
) : Template {

    override val type: TemplateType = TemplateType.LIFESTYLE

    fun updatePeriod(newPeriod: String): LifeStyle {
        return this.copy(period = newPeriod)
    }
}

interface ItemRepository: JpaRepository<Items, Long>

