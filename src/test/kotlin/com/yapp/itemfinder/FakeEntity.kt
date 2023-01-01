package com.yapp.itemfinder

import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.Social
import com.yapp.itemfinder.domain.entity.member.SocialType
import com.yapp.itemfinder.domain.entity.space.SpaceEntity
import java.util.UUID

object FakeEntity {
    fun createFakeMemberEntity(
        id: Long = generateRandomPositiveLongValue(),
        email: String = UUID.randomUUID().toString()
    ): MemberEntity {
        return MemberEntity(
            id = id,
            email = email,
            social = Social(
                socialId = "socialID",
                socialType = SocialType.KAKAO
            ),
            name = "name",
        )
    }
    fun createFakeSpaceEntity(
        id: Long = generateRandomPositiveLongValue(),
        name: String = "방 이름",
        member: MemberEntity,
    ): SpaceEntity {
        return SpaceEntity(
            id = id,
            name = name,
            member = member,
        )
    }
}
