package com.yapp.itemfinder.domain.entity.member

import com.yapp.itemfinder.domain.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "member")
class MemberEntity(
    email: String,
    name: String,
    socialType: SocialType,
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

    @Embedded
    var social: Social = Social(socialType = socialType, socialId = socialId)

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
