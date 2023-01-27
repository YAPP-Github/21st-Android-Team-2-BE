package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemDetailResponse
import com.yapp.itemfinder.domain.tag.ItemTagService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ItemService(
    private val itemRepository: ItemRepository,
    private val containerRepository: ContainerRepository,
    private val itemTagService: ItemTagService
) {
    @Transactional
    fun createItem(request: CreateItemRequest, memberId: Long): ItemDetailResponse {
        val container = containerRepository.findWithSpaceByIdAndMemberId(request.containerId, memberId)
            ?: throw BadRequestException(message = "존재하지 않는 보관함입니다")

        val item = itemRepository.save(
            ItemEntity(
                container = container,
                name = request.name,
                type = ItemType.valueOf(request.itemType),
                quantity = request.quantity,
                dueDate = request.useByDate,
                purchaseDate = request.purchaseDate,
                description = request.description,
                imageUrls = request.imageUrls,
                pinX = request.pinX,
                pinY = request.pinY
            )
        )
        if (request.tagIds.isNotEmpty()) {
            itemTagService.createItemTags(item, request.tagIds, memberId)
        }
        return ItemDetailResponse(item)
    }
}
