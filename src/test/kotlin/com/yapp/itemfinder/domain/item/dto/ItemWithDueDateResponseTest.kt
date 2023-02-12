package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.FakeEntity
import com.yapp.itemfinder.api.exception.InternalServerException
import com.yapp.itemfinder.common.Const.KST_ZONE_ID
import com.yapp.itemfinder.common.DateTimeFormatter.YYYYMMDD
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ItemWithDueDateResponseTest : BehaviorSpec({
    Given("소비기한이 설정된 아이템이 주어졌을 때") {
        val givenLocalDateTime = LocalDateTime.now(KST_ZONE_ID)

        When("오늘 날짜를 기준으로 비교했을 때 아이템의 소비기한이 지났으면") {
            val (passedDays, passedYear) = 4L to 1L
            val daysPassedItem = FakeEntity.createFakeItemEntity(dueDate = givenLocalDateTime.minusDays(passedDays))
            val yearPassedITem = FakeEntity.createFakeItemEntity(dueDate = givenLocalDateTime.minusYears(passedYear))

            Then("지난 일자를 계산해 -N일 형태로 담아 반환한다") {
                val daysPassedResult = ItemWithDueDateResponse.from(daysPassedItem)
                val yearPassedResult = ItemWithDueDateResponse.from(yearPassedITem)

                assertSoftly {
                    daysPassedResult.remainDate shouldBe -1 * passedDays
                    yearPassedResult.remainDate shouldBe -365

                    daysPassedResult.id shouldBe daysPassedItem.id
                    daysPassedResult.name shouldBe daysPassedItem.name
                    daysPassedResult.itemType shouldBe daysPassedItem.type.name
                    daysPassedResult.useByDate shouldBe daysPassedItem.dueDate?.format(YYYYMMDD)
                }
            }
        }

        When("오늘 날짜를 기준으로 비교했을 때 아이템의 소비기한이 남았으면") {
            val remainDate = 4L
            val (remainedDays, remainedYear) = 4L to 1L
            val daysRemainedItem = FakeEntity.createFakeItemEntity(dueDate = givenLocalDateTime.plusDays(remainedDays))
            val yearRemainedItem = FakeEntity.createFakeItemEntity(dueDate = givenLocalDateTime.plusYears(remainedYear))

            Then("남은 일자를 계산해 +N일 형태로 담아 반환한다") {
                val daysRemainedResult = ItemWithDueDateResponse.from(daysRemainedItem)
                val yearRemainedResult = ItemWithDueDateResponse.from(yearRemainedItem)

                assertSoftly {
                    daysRemainedResult.remainDate shouldBe remainDate
                    yearRemainedResult.remainDate shouldBe 365

                    daysRemainedResult.id shouldBe daysRemainedItem.id
                    daysRemainedResult.name shouldBe daysRemainedItem.name
                    daysRemainedResult.itemType shouldBe daysRemainedItem.type.name
                    daysRemainedResult.useByDate shouldBe daysRemainedItem.dueDate?.format(YYYYMMDD)
                }
            }
        }
    }

    Given("소비기한이 설정되지 않은 아이템이 주어졌을 때") {
        val givenItem = FakeEntity.createFakeItemEntity(dueDate = null)

        When("해당 응답으로 변환을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<InternalServerException> {
                    ItemWithDueDateResponse.from(givenItem)
                }
            }
        }
    }
})
