package com.yapp.itemfinder.domain.container

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil.generateRandomString
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ContainerEntityTest : BehaviorSpec({
    Given("보관함을 수정할 때") {
        val givenCurrentSpace = FakeEntity.createFakeSpaceEntity()
        val givenContainer = FakeEntity.createFakeContainerEntity(
            space = givenCurrentSpace,
            name = generateRandomString(4),
            imageUrl = generateRandomString(4),
            iconType = IconType.IC_CONTAINER_1
        )

        val givenNewSpace = FakeEntity.createFakeSpaceEntity()
        val (givenName, givenImageUrl) = generateRandomString(5) to generateRandomString(5)
        val givenIconType = IconType.IC_CONTAINER_5

        When("보관함이 위치한 공간, 이름, 아이콘, 이미지 URL이 주어진다면") {
            val response = givenContainer.update(
                space = givenNewSpace,
                name = givenName,
                imageUrl = givenImageUrl,
                iconType = givenIconType.name
            )

            Then("해당 값들로 보관함 정보를 수정할 수 있다") {
                response.space shouldBe givenNewSpace
                response.name shouldBe givenName
                response.imageUrl shouldBe givenImageUrl
                response.iconType shouldBe givenIconType
            }
        }
    }
})
