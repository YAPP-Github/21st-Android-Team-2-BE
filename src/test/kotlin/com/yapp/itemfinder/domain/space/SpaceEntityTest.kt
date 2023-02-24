package com.yapp.itemfinder.domain.space

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.space.SpaceEntity.Companion.SPACE_NAME_LENGTH_LIMIT
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class SpaceEntityTest : BehaviorSpec({
    Given("공간을 생성할 때") {
        val givenMember = FakeEntity.createFakeMemberEntity()

        When("제한된 글자를 초과하거나 1자 미만 및 공백으로 구성된 공간 이름이 주어지면") {
            val nameOverLengthLimit: String = TestUtil.generateRandomString(SPACE_NAME_LENGTH_LIMIT + 1)
            val givenInvalidNames = listOf(nameOverLengthLimit, "", "  ")

            Then("해당 이름으로 공간을 생성할 수 없다") {
                givenInvalidNames.forAll { givenName ->
                    shouldThrow<BadRequestException> {
                        SpaceEntity(
                            member = givenMember,
                            name = givenName
                        )
                    }
                }
            }
        }

        When("1자 이상 9자 이하의 적절한 공간 이름이 주어지면") {
            val givenValidNames = listOf(TestUtil.generateRandomString(SPACE_NAME_LENGTH_LIMIT), "공간1", "공간2")

            Then("정상적으로 공간을 생성할 수 있다") {
                givenValidNames.forAll { givenName ->
                    shouldNotThrow<Exception> {
                        val space = SpaceEntity(
                            member = givenMember,
                            name = givenName
                        )

                        space.member shouldBe givenMember
                        space.name shouldBe givenName
                    }
                }
            }
        }
    }

    Given("공간을 수정할 때") {
        val givenMember = FakeEntity.createFakeMemberEntity()
        val givenAlreadyExistSpace = SpaceEntity(
            member = givenMember,
            name = "spaceName"
        )

        When("제한된 글자를 초과하거나 1자 미만 및 공백으로 구성된 공간 이름이 주어지면") {
            val nameOverLengthLimit: String = TestUtil.generateRandomString(SPACE_NAME_LENGTH_LIMIT + 1)
            val givenInvalidNames = listOf(nameOverLengthLimit, "", "  ")

            Then("해당 이름으로 공간을 수정할 수 없다") {
                givenInvalidNames.forAll { givenName ->
                    shouldThrow<BadRequestException> {
                        givenAlreadyExistSpace.updateSpace(givenName)
                    }
                }
            }
        }

        When("1자 이상 9자 이하의 적절한 공간 이름이 주어지면") {
            val givenValidNames = listOf(TestUtil.generateRandomString(SPACE_NAME_LENGTH_LIMIT), "공간1", "공간2")

            Then("정상적으로 공간을 수정할 수 있다") {
                givenValidNames.forAll { givenName ->
                    shouldNotThrow<Exception> {
                        val response = givenAlreadyExistSpace.updateSpace(givenName)
                        response.member shouldBe givenMember
                        response.name shouldBe givenName
                    }
                }
            }
        }
    }
})
