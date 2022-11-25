package com.yapp.itemfinder.sample.repository

import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.sample.entity.SampleUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe

@RepositoryTest
class SampleUserRepositoryTest(
    private val sampleUserRepository: SampleUserRepository
) : BehaviorSpec({

    Given("회원의 이름과 이메일을 올바르게 입력한 경우") {
        val sampleUser = SampleUser("user1", "user1@email.com")

        When("회원가입을 하면") {
            val saveUser = sampleUserRepository.save(sampleUser)

            Then("회원이 추가된다") {
                saveUser.email shouldBe sampleUser.email
                saveUser.name shouldBe sampleUser.name
            }
        }
    }

    Given("회원 id가 주어진 경우") {
        val userId = 1L;
        When("존재하는 회원 id라면") {
            sampleUserRepository.save(SampleUser("user1", "user1@email.com", userId))
            Then("회원 정보를 조회할 수 있다") {
                val findUser = sampleUserRepository.findById(userId)
                findUser.shouldBePresent()
                findUser.get().id shouldBe userId
            }
        }
    }
})
