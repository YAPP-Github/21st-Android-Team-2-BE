package com.yapp.itemfinder.sample.entity

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
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
        return ObjectMapper().readValue(dbData, Template::class.java)
    }
}

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Food::class, name = "FOOD"),
    JsonSubTypes.Type(value = LifeStyle::class, name = "LIFESTYLE"),
)
interface Template {
    val name: String
    val description: String
    val type: TemplateType

    enum class TemplateType {
        FOOD, LIFESTYLE
    }
}

// 빈 생성자
data class Food(
    override val name: String = "",
    override val description: String = "",
    override val type: Template.TemplateType = Template.TemplateType.FOOD,
    val taste: String = "",
) : Template

data class LifeStyle(
    override val name: String = "",
    override val description: String = "",
    override val type: Template.TemplateType = Template.TemplateType.LIFESTYLE,
    val period: String = "",
) : Template {
    fun updatePeriod(newPeriod: String): LifeStyle {
        return this.copy(period = newPeriod)
    }
}

interface ItemRepository: JpaRepository<Items, Long>

@RestController
class TestController(
    private val itemRepository: ItemRepository,
    private val productItemRepository: ProductItemRepository
) {
    @Transactional
    @GetMapping("/lib/create")
    fun testWithLibraryCreate() {
        val items = hashMapOf(
            "name" to "샘플 이름",
            "description" to "샘플 설명",
            "tag" to "샘플 태그"
        )

        itemRepository.save(Items.of(items))
    }

    @GetMapping("/lib/find")
    fun testWithLibraryRead() {
        val item = itemRepository.findByIdOrNull(1L) ?: throw RuntimeException("")
        println(item.itemInfo)
    }

    @GetMapping("/custom/create")
    @Transactional
    fun testWithCustomCreate() {
        val food = Food(name = "족발", description = "맛임음", taste = "평범")
        val lifeStyle = LifeStyle(name = "라이프스타일", description = "라이프스타일설명", period ="2")
        productItemRepository.save(ProductItem.of(food))
        productItemRepository.save(ProductItem.of(lifeStyle))
    }

    @GetMapping("/custom/read")
    fun testWithCustomRead() {
        val foodProductItem = productItemRepository.findByIdOrNull(1L) ?: throw RuntimeException("")
        println(foodProductItem.template)

        val lifeStyleItem = productItemRepository.findByIdOrNull(2L) ?: throw RuntimeException("")
        println(lifeStyleItem.template)
    }

    @Transactional
    @GetMapping("/custom/update")
    fun testWithCustomUpdate() {
        val lifeStyleItem = productItemRepository.findByIdOrNull(2L) ?: throw RuntimeException("")
        println(lifeStyleItem.template)
        val lifeStyleTemplate = lifeStyleItem.template as LifeStyle

        val newLifeStyleTemplate = lifeStyleTemplate.updatePeriod("newPeriod")
        lifeStyleItem.updateTemplate(template = newLifeStyleTemplate)
    }
}
