package com.yapp.itemfinder.domain.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class SampleUser(
    name: String,
    email: String,
    id: Long = 0L
) : BaseEntity(id) {

    @Column(nullable = false)
    var name: String = name
        protected set

    @Column(nullable = false)
    var email: String = email
        protected set

    fun update(name: String?, email: String?) {
        if (name != null) {
            this.name = name
        }
        if (email != null) {
            this.email = email
        }
    }
}
