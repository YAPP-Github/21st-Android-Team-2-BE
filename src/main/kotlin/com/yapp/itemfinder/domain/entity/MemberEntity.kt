package com.yapp.itemfinder.domain.entity

import javax.persistence.*

@Entity
@Table(name = "member")
class MemberEntity(
    email: String,
    name: String,
    socialType: String,
    socialId: String,
    status: MemberStatus = MemberStatus.ACTIVE,
    id: Long = 0L
) : BaseEntity(id) {

    @Column(length = 100, nullable = false, unique = true)
    var email: String = email
        protected set

    @Column(length = 50, nullable = false)
    var name: String = name
        protected set

    @Column(length = 20, nullable = false)
    var socialType: String = socialType
        protected set

    @Column(length = 20, nullable = false)
    var socialId: String = socialId
        protected set

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: MemberStatus = status
        protected set

}

enum class MemberStatus {
    ACTIVE, INACTIVE
}
