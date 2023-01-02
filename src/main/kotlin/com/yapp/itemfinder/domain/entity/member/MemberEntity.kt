package com.yapp.itemfinder.domain.entity.member

import com.yapp.itemfinder.domain.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "member",
    indexes = [
        Index(name = "idx_email", columnList = "email"),
        Index(name = "uk_social", columnList = "social_id, social_type", unique = true)
    ]
)
class MemberEntity(
    email: String,
    name: String,
    social: Social,
    status: MemberStatus = MemberStatus.ACTIVE,
    id: Long = 0L
) : BaseEntity(id) {

    @Column(length = 100)
    var email: String = email
        protected set

    @Column(length = 50, nullable = false)
    var name: String = name
        protected set

    @Embedded
    var social: Social = social
        protected set

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: MemberStatus = status
        protected set
}

enum class MemberStatus {
    ACTIVE, INACTIVE
}

@Embeddable
class Social(
    socialType: SocialType,
    socialId: String
) {
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, name = "social_type")
    var socialType: SocialType = socialType
        protected set

    @Column(length = 20, nullable = false, name = "social_id")
    var socialId: String = socialId
        protected set
}

enum class SocialType {
    KAKAO
}
