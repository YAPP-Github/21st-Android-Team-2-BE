package com.yapp.itemfinder.sample.entity

import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.JpaRepository
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table


@MappedSuperclass
class AbstractBaseThing(
    val name: String,
    val description: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L


    @CreatedDate
    @Column(updatable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now())

    @LastModifiedDate
    @Column(updatable = true)
    var lastModifiedAt: Timestamp = Timestamp.from(Instant.now())
}


interface SpaceRepository: JpaRepository<Space, Long>
interface ProductRepository: JpaRepository<Product, Long>
interface SingleItemRepository: JpaRepository<SingleItem, Long>

@Entity
@Table(name = "poc_space")
class Space(
    name: String,
    id: Long = 0L
) : BaseEntity(id) {

    @Column(nullable = false)
    var name: String = name
        protected set
}

@Entity
@DynamicUpdate
@Table(name = "poc_product")
class Product(
    id: Long = 0L,
    space: Space,
    type: ProductType,
    name: String,
    description: String,
    parentProduct: Product? = null,
    childProducts: MutableList<Product> = mutableListOf<Product>(),
    item: SingleItem? = null
): BaseEntity(id) {
    @ManyToOne(fetch = FetchType.LAZY)
    var space: Space = space
        protected set

    var name: String = name
        protected set

    var description: String = description
        protected set

    @Enumerated(EnumType.STRING)
    var type: ProductType = type
        protected set

    @ManyToOne
    var parentProduct: Product? = parentProduct
        protected set

    @OneToMany(mappedBy = "parentProduct")
    var childProducts: MutableList<Product> = childProducts
        protected set

    @OneToOne(optional = true)
    var item: SingleItem? = item
        protected set

    // 아이템 타입이라면 저장하는 정보가 많다
    fun updateChilds(childProducts: List<Product>) {
        this.childProducts = childProducts.toMutableList()
    }
}

@Entity
@Table(name = "poc_item")
class SingleItem(
    id: Long = 0L,
    category: String,
    template: Template
): BaseEntity(id){
    @Convert(converter = TemplateConverter::class)
    var template: Template = template
        protected set

    var category: String = category
        protected set

    fun updateTemplate(template: Template) {
        this.template = template
    }
}
enum class ProductType {
    BOX, ITEM
}

