package com.yapp.itemfinder.domain.entity.space

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.TestUtil
import com.yapp.itemfinder.api.exception.BadRequestException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class SpaceEntityTest : BehaviorSpec({
    Given("공간을 생성할 때") {
        val givenMember = FakeEntity.createFakeMemberEntity()

        When("30자를 초과하거나 1자 미만 및 공백으로 구성된 공간 이름이 주어지면") {
            val nameOverLengthLimit: String = TestUtil.generateRandomString(31)
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

        When("1자 이상 30자 이하의 적절한 공간 이름이 주어지면") {
            val givenValidNames = listOf(TestUtil.generateRandomString(30), "공간1", "공간2")

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
})
