package com.yapp.itemfinder.sample.entity

import javax.persistence.*

@Entity
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
@DiscriminatorValue("FOOD")
class FoodSingleTable(
    category: String,
    name: String,
    description: String,
    taste: String = "",
    gram: Int = 0,
    id: Long = 0L,
) : Item(category, name, description, id) {

    var taste = taste
        protected set

    var gram = gram
        protected set
}

@Entity
@DiscriminatorValue("LIFESTYLE")
class LifeSingleTable(
    category: String,
    name: String,
    description: String,
    period: String = "",
    id: Long = 0L,
) : Item(category, name, description, id) {

    var period = period
        protected set
}
