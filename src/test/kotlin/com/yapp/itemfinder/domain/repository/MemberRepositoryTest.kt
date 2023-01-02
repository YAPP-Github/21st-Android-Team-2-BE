package com.yapp.itemfinder.domain.repository

import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.member.Social
import com.yapp.itemfinder.domain.entity.member.SocialType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@RepositoryTest
class MemberRepositoryTest(
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    Given("회원이 존재하는 경우") {
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)
        memberRepository.save(MemberEntity(email = "test@email.com", name = "member", social = social))

        When("social 정보로 회원을 조회하면") {
            val member = memberRepository.findBySocial(social)

            Then("조회에 성공한다") {
                member shouldNotBe null
                member?.social?.socialId shouldBe socialId
            }
        }
    }

    Given("회원이 존재하지 않는 경우") {
        val socialId = "123456789"
        val social = Social(SocialType.KAKAO, socialId)

        When("social 정보로 회원을 조회하면") {
            val member = memberRepository.findBySocial(social)

            Then("조회에 실패한다") {
                member shouldBe null
            }
        }
    }
})
