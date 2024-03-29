package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.FakeEntity.createFakeItemEntity
import com.yapp.itemfinder.RepositoryTest
import com.yapp.itemfinder.TestCaseUtil
import com.yapp.itemfinder.TestUtil.generateRandomPositiveLongValue
import com.yapp.itemfinder.TestUtil.generateRandomString
import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.common.Const.KST_ZONE_ID
import com.yapp.itemfinder.domain.item.dto.ItemDueDateTarget
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SortOrderOption
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
            val searchOption = ItemSearchOption(
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
            val searchOption = ItemSearchOption(
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

    Given("2개의 태그가 설정된 아이템이 저장되어 있을 때") {
        val givenPageable = PageRequest.of(0, 20)
        val (givenMember, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()

        val (givenItemName, givenFirstTagName, givenSecondTagName) = Triple(generateRandomString(10), generateRandomString(10), generateRandomString(10))
        val givenItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenItemName))

        testCaseUtil.`한 개의 아이템에 전달받은 태그 이름들에 대한 태그 등록`(item = givenItem, tagNames = listOf(givenFirstTagName, givenSecondTagName), member = givenMember)

        When("태그 이름에 대한 필터를 설정하고 조회한다면") {
            val searchOptionContainSavedTags = ItemSearchOption(tagNames = listOf(givenFirstTagName, givenSecondTagName))
            val searchOptionContainUnsavedTags = ItemSearchOption(tagNames = listOf(generateRandomString(10), givenFirstTagName, givenSecondTagName))

            val searchResultContainSavedTags = itemRepository.search(searchOptionContainSavedTags, givenPageable, targetContainerIds = listOf(givenContainer.id))
            val searchResultContainUnsavedTags = itemRepository.search(searchOptionContainUnsavedTags, givenPageable, targetContainerIds = listOf(givenContainer.id))

            Then("해당 태그를 모두 보유한 아이템만 조회된다") {
                searchResultContainSavedTags.content.size shouldBe 1
                searchResultContainSavedTags.totalElements shouldBe 1
                searchResultContainSavedTags.totalPages shouldBe 1
                searchResultContainSavedTags.content shouldContain givenItem

                searchResultContainUnsavedTags.content.size shouldBe 0
                searchResultContainUnsavedTags.totalElements shouldBe 0
                searchResultContainUnsavedTags.totalPages shouldBe 0
            }
        }

        When("아이템 이름에 대한 필터를 설정했을 때 해당 이름을 포함하고 있는 아이템이 존재한다면") {
            val searchOption = ItemSearchOption(
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
            val searchOption = ItemSearchOption(
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
                ItemSearchOption(), givenPageable, targetContainerIds = listOf(generateRandomPositiveLongValue())
            )

            Then("해당 아이템이 조회되지 않는다") {
                result.content.size shouldBe 0
                result.totalElements shouldBe 0
                result.totalPages shouldBe 0
            }
        }

        When("아이디로 아이템을 찾으면") {
            val item = itemRepository.findByIdWithContainerAndSpace(givenItem.id)

            Then("해당 아이템이 조회된다") {
                item shouldNotBe null
                item shouldBe givenItem
            }
        }
    }

    Given("2개의 이름이 설정된 아이템이 저장되어 있을 때") {
        val (_, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()
        val (givenFirstItemName, givenSecondItemName) = "가나다" to "마바사"
        val givenFirstItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenFirstItemName)).also {
            it.createdAt = LocalDateTime.now().minusDays(1)
        }
        val givenSecondItem = itemRepository.save(createFakeItemEntity(container = givenContainer, name = givenSecondItemName))

        When("이름 오름차 순으로 조회한다면") {
            val searchOption = ItemSearchOption(sortOrderOption = SortOrderOption.NameAsc)
            val result = itemRepository.search(searchOption, PageRequest.of(0, 20, searchOption.getSort()), targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenFirstItem, givenSecondItem)
            }
        }

        When("이름 내림차 순으로 조회한다면") {
            val searchOption = ItemSearchOption(sortOrderOption = SortOrderOption.NameDesc)
            val result = itemRepository.search(searchOption, PageRequest.of(0, 20, searchOption.getSort()), targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenSecondItem, givenFirstItem)
            }
        }

        When("예전에 생성된 시간 순으로 조회한다면") {
            val searchOption = ItemSearchOption(sortOrderOption = SortOrderOption.PastCreated)
            val result = itemRepository.search(searchOption, PageRequest.of(0, 20, searchOption.getSort()), targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenFirstItem, givenSecondItem)
            }
        }

        When("최근에 생성된 시간 순으로 조회한다면") {
            val searchOption = ItemSearchOption(sortOrderOption = SortOrderOption.RecentCreated)
            val result = itemRepository.search(searchOption, PageRequest.of(0, 20, searchOption.getSort()), targetContainerIds = listOf(givenContainer.id))

            Then("해당 순서대로 정렬해서 반환한다") {
                result.content.size shouldBe 2
                result.content shouldContainInOrder listOf(givenSecondItem, givenFirstItem)
            }
        }

        When("찾을 대상으로 전달받은 보관함 아이디가 없다면") {
            val emptyContainers = emptyList<Long>()

            val result = itemRepository.search(ItemSearchOption(), PageRequest.of(0, 20), emptyContainers)

            Then("아이템이 조회되지 않는다") {
                result.content.size shouldBe 0
                result.content shouldBe emptyList()
            }
        }
    }

    Given("회원이 아이템을 등록하지 않은 경우") {
        When("아이디로 아이템을 찾으면") {
            val itemId = generateRandomPositiveLongValue()

            Then("예외가 발생한다") {
                shouldThrow<NotFoundException> {
                    itemRepository.findByIdWithContainerAndSpaceOrThrowException(itemId)
                }
            }
        }
    }

    Given("소비기한이 설정된 아이템들이 저장되어 있을 때") {
        val givenPageSize = 2
        val (givenFirstPageNumber, givenSecondPageNumber) = 0 to 1
        val (givenFirstPageable, givenSecondPageable) = PageRequest.of(givenFirstPageNumber, givenPageSize) to PageRequest.of(givenSecondPageNumber, givenPageSize)

        val (givenMember, givenContainer) = testCaseUtil.`한 명의 회원과 해당 회원이 저장한 보관함 반환`()
        val (givenPassedDueDateCount, givenRemainedDueDateCount) = 3 to 2
        val today = LocalDateTime.now(KST_ZONE_ID)

        repeat(givenPassedDueDateCount) {
            itemRepository.save(createFakeItemEntity(container = givenContainer, type = ItemType.LIFE, dueDate = today.minusDays(it.toLong() + 1)))
        }

        repeat(givenRemainedDueDateCount) {
            itemRepository.save(createFakeItemEntity(container = givenContainer, type = ItemType.LIFE, dueDate = today.plusDays(it.toLong())))
        }

        When("소비기한이 지난 아이템을 조회한다면") {
            val passedDueDateTarget = ItemDueDateTarget.PASSED
            val firstPageResult = itemRepository.searchByDueDate(pageable = givenFirstPageable, memberId = givenMember.id, dueDateTarget = passedDueDateTarget)
            val secondPageResult = itemRepository.searchByDueDate(pageable = givenSecondPageable, memberId = givenMember.id, dueDateTarget = passedDueDateTarget)

            Then("대상 아이템을 소비기한이 오래된 순으로 정렬하고 페이지 수만큼 페이징을 진행해서 결과를 반환한다") {
                assertSoftly {
                    firstPageResult.totalPages shouldBe 2
                    firstPageResult.totalElements shouldBe givenPassedDueDateCount

                    firstPageResult.content.size shouldBeLessThan givenPassedDueDateCount
                    firstPageResult.content.size shouldBe givenFirstPageable.pageSize
                    with(firstPageResult.content) {
                        this[0].dueDate shouldBe today.minusDays(3)
                        this[1].dueDate shouldBe today.minusDays(2)
                    }

                    secondPageResult.totalPages shouldBe 2
                    secondPageResult.totalElements shouldBe givenPassedDueDateCount
                    secondPageResult.content.size shouldBe givenPassedDueDateCount - givenFirstPageable.pageSize
                    secondPageResult.content[0].dueDate shouldBe today.minusDays(1)
                }
            }
        }

        When("소비기한이 남은 아이템을 조회한다면") {
            val remainedDueDateTarget = ItemDueDateTarget.REMAINED
            val result = itemRepository.searchByDueDate(pageable = givenFirstPageable, memberId = givenMember.id, dueDateTarget = remainedDueDateTarget)

            Then("대상 아이템을 소비기한이 오래된 순으로 정렬하고 페이지 수만큼 페이징을 진행해서 결과를 반환한다") {
                result.totalPages shouldBe 1
                result.totalElements shouldBe givenRemainedDueDateCount
                result.content.size shouldBe givenRemainedDueDateCount

                with(result.content) {
                    this[0].dueDate shouldBe today.plusDays(0)
                    this[1].dueDate shouldBe today.plusDays(1)
                }
            }
        }
    }
})
