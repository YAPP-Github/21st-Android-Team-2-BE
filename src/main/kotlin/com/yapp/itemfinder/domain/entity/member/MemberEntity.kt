package com.yapp.itemfinder.domain.entity.member

import com.yapp.itemfinder.domain.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "member",
    indexes = [
        Index(name = "idx_email", columnList = "email"),
        Index(name = "idx_social_id", columnList = "socialId")
    ]
)
class MemberEntity(
    email: String,
    name: String,
    social: Social,
    status: MemberStatus = MemberStatus.ACTIVE,
    id: Long = 0L
) : BaseEntity(id) {

    @Column(length = 100, nullable = false)
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
    @Column(length = 20, nullable = false)
    var socialType: SocialType = socialType
        protected set
    @Column(length = 20, nullable = false)
    var socialId: String = socialId
        protected set
}

enum class SocialType {
    KAKAO
}
