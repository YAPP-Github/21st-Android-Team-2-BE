package com.yapp.itemfinder

import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.Social
import com.yapp.itemfinder.domain.entity.member.SocialType
import java.util.UUID

object FakeEntity {
    fun getMemberEntity(
        email: String = UUID.randomUUID().toString(),
    ): MemberEntity {
        return MemberEntity(
            email = email,
            social = Social(
                socialId = "socialID",
                socialType = SocialType.KAKAO
            ),
            name = "name"
        )
    }
}
