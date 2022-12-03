package com.yapp.itemfinder.sample.entity

import javax.persistence.Column
import javax.persistence.DiscriminatorColumn
import javax.persistence.DiscriminatorType
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.Table

enum class SingleItemType(value: String) {
    LIFESTYLE(Values.LIFESTYLE), FOOD(Values.FOOD);

    object Values {
        const val LIFESTYLE = "LIFESTYLE"
        const val FOOD = "FOOD"
    }
}

@Entity
@Table(name = "single_test")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
abstract class Item(
    category: String,
    name: String,
    description: String,
    id: Long = 0L,
): BaseEntity(id){

    var name: String = name
        protected set

    var description: String = description
        protected set

    var category: String = category
        protected set

    @Column(insertable = false, updatable = false)
    var type: String = ""
}

@Entity
@DiscriminatorValue(SingleItemType.Values.FOOD)
class FoodSingleTable(
    category: String,
    name: String,
    description: String,
    taste: String = "",
    gram: Int = 0,
    id: Long = 0L,
) : Item(
    category = category,
    name = name,
    description = description,
    id = id) {

    var taste = taste
        protected set

    var gram = gram
        protected set
}

@Entity
@DiscriminatorValue(SingleItemType.Values.LIFESTYLE)
class LifeSingleTable(
    category: String,
    name: String,
    description: String,
    period: String = "",
    id: Long = 0L,
) : Item(category = category,
    name = name,
    description = description,
    id = id) {

    var period = period
        protected set
}
