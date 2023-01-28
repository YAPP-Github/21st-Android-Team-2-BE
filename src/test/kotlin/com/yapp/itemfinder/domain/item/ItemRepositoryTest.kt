package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.TestCaseUtil
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.domain.item.dto.SearchOption
import com.yapp.itemfinder.domain.item.dto.SortOrderOption
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@RepositoryTest
class ItemRepositoryTest(
    private val itemRepository: ItemRepository,
    private val testCaseUtil: TestCaseUtil
) : BehaviorSpec({

    Given("21개의 아이템이 10개는 패션으로, 5개는 라이프스타일로, 6개는 음식으로 등록되었다면") {
        val (fashionItemCount, lifeItemCount, foodItemCount) = Triple(10, 5, 6)
        val givenPageable = PageRequest.of(0, 20)
        val (_, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()

        repeat(fashionItemCount) {
            itemRepository.save(createFakeItemEntity(container = givenContainer, type = ItemType.FASHION))
        }
        repeat(lifeItemCount) {
            itemRepository.save(createFakeItemEntity(container = givenContainer, type = ItemType.LIFE))
        }
        repeat(foodItemCount) {
            itemRepository.save(createFakeItemEntity(container = givenContainer, type = ItemType.FOOD))
        }

        When("아이템 타입 필터 조건 없이 페이징 데이터를 요청한다면") {
            val searchOption = SearchOption(
                itemTypes = emptyList()
            )
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("카테고리 필터 없이 페이지 수만큼 페이징을 진행해서 결과를 반환한다") {
                result.content.size shouldBe givenPageable.pageSize
                result.totalElements shouldBe fashionItemCount + lifeItemCount + foodItemCount
                result.totalPages shouldBe 2
            }
        }

        When("아이템 타입 필터 조건을 포함해서 페이징 데이터를 요청한다면") {
            val searchOption = SearchOption(
                itemTypes = listOf(ItemType.FASHION, ItemType.LIFE)
            )
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("전달받은 아이템 타입에 해당하는 아이템만 조회한다") {
                result.content.size shouldBe fashionItemCount + lifeItemCount
                result.totalElements shouldBe fashionItemCount + lifeItemCount
                result.totalPages shouldBe 1
            }
        }
    }

    Given("보관함에 2개의 태그를 설정한 아이템이 저장되어 있을 때") {
        val givenPageable = PageRequest.of(0, 20)
        val (givenMember, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()
        val (givenItemName, givenFirstTagName, givenSecondTagName) = Triple(generateRandomString(10), generateRandomString(10), generateRandomString(10))

        val givenItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenItemName))
        testCaseUtil.`한 개의 아이템에 전달받은 태그 이름들에 대한 태그 등록`(item = givenItem, tagNames = listOf(givenFirstTagName, givenSecondTagName), member = givenMember)

        When("태그 이름에 대한 필터를 설정하고 조회한다면") {
            val searchOptionWithSavedTags = SearchOption(tagNames = listOf(givenFirstTagName, givenSecondTagName))
            val searchOptionWithUnsavedTags = SearchOption(tagNames = listOf(generateRandomString(10), givenFirstTagName, givenSecondTagName))
            val searchResultWithSavedTags = itemRepository.search(searchOptionWithSavedTags, givenPageable, targetContainerIds = listOf(givenContainer.id))
            val searchResultWithUnsavedTags = itemRepository.search(searchOptionWithUnsavedTags, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 태그를 모두 보유한 아이템만 조회된다") {
                searchResultWithSavedTags.content.size shouldBe 1
                searchResultWithSavedTags.totalElements shouldBe 1
                searchResultWithSavedTags.totalPages shouldBe 1
                searchResultWithSavedTags.content shouldContain givenItem

                searchResultWithUnsavedTags.content.size shouldBe 0
                searchResultWithUnsavedTags.totalElements shouldBe 0
                searchResultWithUnsavedTags.totalPages shouldBe 0
            }
        }

        When("아이템 이름에 대한 필터를 설정했을 때 해당 이름을 포함하고 있는 아이템이 존재한다면") {
            val searchOption = SearchOption(
                tagNames = listOf(givenFirstTagName, givenSecondTagName),
                itemName = givenItemName.substring(2 until 5)
            )

            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 아이템이 조회된다") {
                result.content shouldContain givenItem
                result.content.size shouldBe 1
                result.totalElements shouldBe 1
                result.totalPages shouldBe 1
            }
        }

        When("아이템 이름에 대한 필터를 설정했을 때 해당 이름을 포함하고 있는 아이템이 존재하지 않는다면") {
            val searchOption = SearchOption(
                itemName = givenItemName.substring(2 until 5).plus(generateRandomString(20))
            )
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("어떤 아이템도 조회되지 않는다") {
                result.content.size shouldBe 0
                result.totalElements shouldBe 0
                result.totalPages shouldBe 0
            }
        }

        When("아이템이 존재하지 않는 보관함에 대한 아이디로 조회한다면") {
            val result = itemRepository.search(
                SearchOption(), givenPageable, targetContainerIds = listOf(generateRandomPositiveLongValue())
            )

            Then("해당 아이템이 조회되지 않는다") {
                result.content.size shouldBe 0
                result.totalElements shouldBe 0
                result.totalPages shouldBe 0
            }
        }
    }

    Given("보관함에 2개의 아이템이 저장되어 있을 때") {
        val (_, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()
        val (givenFirstItemName, givenSecondItemName) = "가나다" to "마바사"
        val givenFirstItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenFirstItemName)).also {
            it.createdAt = LocalDateTime.now().minusDays(1)
        }
        val givenSecondItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenSecondItemName))

        When("이름 오름차 순으로 조회한다면") {
            val searchOption = SearchOption(
                sortOrderOption = SortOrderOption.NameAsc
            )
            val givenPageable = PageRequest.of(0, 20, searchOption.getSort())
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenFirstItem, givenSecondItem)
            }
        }

        When("이름 내림자 순으로 조회한다면") {
            val searchOption = SearchOption(
                sortOrderOption = SortOrderOption.NameDesc
            )
            val givenPageable = PageRequest.of(0, 20, searchOption.getSort())
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenSecondItem, givenFirstItem)
            }
        }

        When("예전에 생성된 시간 순으로 조회한다면") {
            val searchOption = SearchOption(
                sortOrderOption = SortOrderOption.PastCreated
            )
            val givenPageable = PageRequest.of(0, 20, searchOption.getSort())
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenFirstItem, givenSecondItem)
            }
        }

        When("최근에 생성된 시간 순으로 조회한다면") {
            val searchOption = SearchOption(
                sortOrderOption = SortOrderOption.RecentCreated
            )
            val givenPageable = PageRequest.of(0, 20, searchOption.getSort())
            val result = itemRepository.search(searchOption, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenSecondItem, givenFirstItem)
            }
        }
    }
})