package com.yapp.itemfinder.domain.service

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.entity.SampleUser
import com.yapp.itemfinder.domain.repository.SampleUserRepository
import com.yapp.itemfinder.domain.service.dto.CreateUserReq
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class SampleUserServiceTest : BehaviorSpec({
    val sampleUserRepository = mockk<SampleUserRepository>()
    val sampleUserService = SampleUserService(sampleUserRepository)

    Given("처음으로 회원가입을 하는 사람인 경우") {
        val req = CreateUserReq(name = "user1", email = "user1@email.com")

        every { sampleUserRepository.existsByEmail(req.email) } returns false
        every { sampleUserRepository.save(any()) } returns SampleUser(req.name, req.email, 1L)

        When("회원가입을 하면") {
            val res = sampleUserService.insertUser(req)

            Then("회원가입에 성공하고 추가된 id를 반환받는다") {
                res.id shouldNotBe null
                res.id shouldBe 1L
            }
        }
    }

    Given("이미 가입한 경우") {
        val req = CreateUserReq(name = "user1", email = "user1@email.com")
        every { sampleUserRepository.existsByEmail(req.email) } returns true

        When("회원가입을 하면") {
            Then("BadRequestException 예외가 발생한다") {
                shouldThrow<BadRequestException> { sampleUserService.insertUser(req) }
            }
        }
    }
})
