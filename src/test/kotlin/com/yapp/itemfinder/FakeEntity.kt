package com.yapp.itemfinder

import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.space.SpaceEntity
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
                socialId = generateRandomString(20),
                socialType = SocialType.KAKAO
            ),
            name = "name",
        )
    }
    fun createFakeSpaceEntity(
        id: Long = generateRandomPositiveLongValue(),
        name: String = "공간 이름",
        member: MemberEntity,
    ): SpaceEntity {
        return SpaceEntity(
            id = id,
            name = name,
            member = member,
        )
    }
}
