package com.yapp.itemfinder.sample.entity

import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.KClass

@RestController
class TestController(
    private val itemRepository: ItemRepository,
    private val productItemRepository: ProductItemRepository,
    private val spaceRepository: SpaceRepository,
    private val productRepository: ProductRepository,
    private val singleItemRepository: SingleItemRepository
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

//    @GetMapping("/lib/find")
//    fun testWithLibraryRead() {
//        val item = itemRepository.findByIdOrNull(1L) ?: throw RuntimeException("")
//        println(item.itemInfo)
//    }
//
//    @GetMapping("/custom/create")
//    @Transactional
//    fun testWithCustomCreate() {
//        val food = Food(name = "족발", description = "맛임음", taste = "평범")
//        val lifeStyle = LifeStyle(name = "라이프스타일", description = "라이프스타일설명", period ="2")
//        productItemRepository.save(ProductItem.of(food))
//        productItemRepository.save(ProductItem.of(lifeStyle))
//    }
//
//    @GetMapping("/custom/read")
//    fun testWithCustomRead() {
//        val foodProductItem = productItemRepository.findByIdOrNull(1L) ?: throw RuntimeException("")
//        println(foodProductItem.template)
//
//        val lifeStyleItem = productItemRepository.findByIdOrNull(2L) ?: throw RuntimeException("")
//        println(lifeStyleItem.template)
//    }
//
//    @Transactional
//    @GetMapping("/custom/update")
//    fun testWithCustomUpdate() {
//        val lifeStyleItem = productItemRepository.findByIdOrNull(2L) ?: throw RuntimeException("")
//        println(lifeStyleItem.template)
//        val lifeStyleTemplate = lifeStyleItem.template as LifeStyle
//
//        val newLifeStyleTemplate = lifeStyleTemplate.updatePeriod("newPeriod")
//        lifeStyleItem.updateTemplate(template = newLifeStyleTemplate)
//    }
    @Transactional
    @GetMapping("/test")
    fun test() {
        val space = Space(name = "test")
        spaceRepository.save(space)

        val topProduct = Product(
            space = space,
            type = ProductType.BOX,
            name = "박스",
            description = "박스입니다",
        )
        productRepository.save(topProduct)

        val foodItem = Food(taste = "평범", gram = 1000)
        val lifeStyleItem = LifeStyle(period ="2")

        val singleFoodItem = SingleItem(category = "이유식", template = foodItem, name = "식품 이름", description = "식품 설명", type = TemplateType.FOOD)
        val singleLifeStyleItem = SingleItem(category = "공구", template = lifeStyleItem, name = "생활 이름", description = "생활 설명", type = TemplateType.LIFESTYLE)

        singleItemRepository.save(singleFoodItem)
        singleItemRepository.save(singleLifeStyleItem)

        val productFoodItem = Product(
            space = space,
            type = ProductType.ITEM,
            name = "식품",
            description = "식품 아이템입니다",
            parentProduct = topProduct,
            item = singleFoodItem
        )

        val productLifestyleItem = Product(
            space = space,
            type = ProductType.ITEM,
            name = "생활용품1",
            description = "생활용품1 아이템입니다",
            parentProduct = topProduct,
            item = singleLifeStyleItem
        )

        val subProductBox = Product(
            space = space,
            type = ProductType.BOX,
            name = "서브 박스",
            description = "서브 박스입니다",
            parentProduct = topProduct
        )

        productRepository.save(productFoodItem)
        productRepository.save(productLifestyleItem)
        productRepository.save(subProductBox)
        topProduct.updateChilds(listOf(productFoodItem, productLifestyleItem,subProductBox))
    }

    @Transactional
    @GetMapping("/test/get")
    fun get(): List<ItemDto> {
        val product = productRepository.findByIdOrNull(1L) ?: throw RuntimeException("")
        val childProducts = product.childProducts

        println(childProducts.map { it.name })
        val productItems = childProducts.filter { it.type == ProductType.ITEM }
        val items = productItems.mapNotNull { it.item }
        return items.map { ItemDto(name = it.name, description = it.description, type = it.type, template = it.template) }


//        val templates = items.map { it.template }
//        val template = templates.first()
//        ItemDto()
//        if (template is Food) {
//            val foodDto = FoodDto(template as Food)
//        }
    }



    @GetMapping("/products/{id}")
    fun findById(@PathVariable id: Long) {
        val item = productRepository.findByIdOrNull(id) ?: throw RuntimeException("")

    }
}
data class ItemDto(
    val name: String,
    val description: String,
    val type: TemplateType,
    val template: Template
) {
}

//data class FoodDto(
//    val name: String,
//    val description: String,
//    val taste: String,
//) {
//    constructor(food: Food): this(food.name, food.description, food.taste)
//}

/**
 *
 *  val declaringClass = template.javaClass.declaringClass
val kotlin1: KClass<out Any> = declaringClass.kotlin
println("kclass =$kotlin1 declaringClass= $declaringClass")
val kotlin = Class.forName("com.yapp.itemfinder.sample.entity.Food").kotlin

println(template.toString())
println(template.javaClass)
// TODO 해당 아이템의 template을 식별해서 특정 클래스로 downcast

 */
