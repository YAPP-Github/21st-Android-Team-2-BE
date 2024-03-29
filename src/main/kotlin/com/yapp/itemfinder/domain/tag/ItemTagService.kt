package com.yapp.itemfinder.domain.tag

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.item.ItemEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ItemTagService(
    private val tagRepository: TagRepository,
    private val itemTagRepository: ItemTagRepository
) {
    @Transactional
    fun createItemTags(item: ItemEntity, tagIds: List<Long>, memberId: Long): List<ItemTagEntity> {
        val distinctTagIds = tagIds.distinct()
        val itemTags = tagRepository.findByIdIsInAndMemberId(distinctTagIds, memberId)
            .also {
                require(it.size == distinctTagIds.size) {
                    throw BadRequestException(message = "존재하지 않는 태그 아이디입니다.")
                }
            }
            .map { itemTagRepository.save(ItemTagEntity(item = item, tag = it)) }
        item.tags.addAll(itemTags)
        return itemTags
    }

    fun createItemIdToTagNames(itemIds: List<Long>): Map<Long, List<String>> {
        if (itemIds.isEmpty()) {
            return emptyMap()
        }

        return itemTagRepository.findItemIdAndTagNameByItemIdIsIn(itemIds)
            .groupBy { it.itemId }
            .mapValues { (_, itemTagNames) -> itemTagNames.map { it.tagName } }
    }

    @Transactional
    fun deleteItemTagsByItems(items: List<ItemEntity>) {
        itemTagRepository.deleteAllByItemIsIn(items)
    }
}
