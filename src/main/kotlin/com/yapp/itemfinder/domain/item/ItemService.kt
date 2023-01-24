package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemResponse
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
    fun createItem(request: CreateItemRequest, memberId: Long): ItemResponse {
        val container = containerRepository.findWithSpaceByIdAndMemberId(request.containerId, memberId)
            ?: throw BadRequestException(message = "존재하지 않는 보관함입니다")

        val item = itemRepository.save(
            ItemEntity(
                container = container,
                name = request.name,
                type = ItemType.valueOf(request.category),
                quantity = request.quantity,
                dueDate = request.useByDate,
                purchaseDate = request.purchaseDate,
                description = request.description,
                imageUrls = request.imageUrls.map { it.url } as MutableList<String>,
                itemPin = request.pinX?.let { x -> request.pinY?.let { y -> ItemPin(x, y) } }
            )
        )
        if (request.tagIds.isNotEmpty()) {
            itemTagService.createItemTags(item, request.tagIds, memberId)
        }
        return ItemResponse(item)
    }
}
