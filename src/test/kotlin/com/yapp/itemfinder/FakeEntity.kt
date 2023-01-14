package com.yapp.itemfinder

import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.IconType
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.member.Social
import com.yapp.itemfinder.domain.member.SocialType
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.SpaceEntity.Companion.SPACE_NAME_LENGTH_LIMIT
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
        name: String = generateRandomString(SPACE_NAME_LENGTH_LIMIT),
        member: MemberEntity = createFakeMemberEntity(),
    ): SpaceEntity {
        return SpaceEntity(
            id = id,
            name = name,
            member = member,
        )
    }

    fun createFakeContainerEntity(
        id: Long = generateRandomPositiveLongValue(),
        name: String = "컨테이너 이름",
        space: SpaceEntity,
        iconType: IconType = IconType.IC_CONTAINER_1,
        imageUrl: String = "image URL"
    ): ContainerEntity {
        return ContainerEntity(
            id = id,
            name = name,
            space = space,
            iconType = iconType,
            imageUrl = imageUrl
        )
    }
}
