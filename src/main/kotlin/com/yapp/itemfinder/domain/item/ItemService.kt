package com.yapp.itemfinder.domain.item

import com.yapp.itemfinder.common.PageResponse
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.exception.ForbiddenException
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemDetailResponse
import com.yapp.itemfinder.domain.item.dto.ItemOverviewResponse
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SearchTarget
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SearchTarget.SearchLocation.CONTAINER
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption.SearchTarget.SearchLocation.SPACE
import com.yapp.itemfinder.domain.item.dto.UpdateItemRequest
import com.yapp.itemfinder.support.PermissionValidator
import com.yapp.itemfinder.domain.tag.ItemTagService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ItemService(
    private val itemRepository: ItemRepository,
    private val containerRepository: ContainerRepository,
    private val itemTagService: ItemTagService,
    private val permissionValidator: PermissionValidator
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

    fun findItem(itemId: Long, memberId: Long): ItemDetailResponse {
        val item = findMemberItemOrThrowException(itemId, memberId)
        return ItemDetailResponse(item)
    }

    fun search(searchOption: ItemSearchOption, pageRequest: PageRequest, memberId: Long): PageResponse<ItemOverviewResponse> {
        val targetContainerIds = searchOption.searchTarget?.let {
            findSearchTargetContainerIds(it.location, memberId, it.id)
        } ?: containerRepository.findByMemberId(memberId).map { it.id }

        val pagedItems: Page<ItemEntity> = itemRepository.search(
            searchOption = searchOption,
            pageable = pageRequest,
            targetContainerIds = targetContainerIds
        )

        val itemIdToTagNames = itemTagService.createItemIdToTagNames(itemIds = pagedItems.content.map { it.id })
        val maxTagNumberPerItem = 4
        return PageResponse(
            page = pagedItems.map {
                val tagNames = itemIdToTagNames.getOrDefault(it.id, emptyList()).take(maxTagNumberPerItem)
                ItemOverviewResponse(it, tagNames)
            }
        )
    }

    private fun findSearchTargetContainerIds(searchLocation: SearchTarget.SearchLocation, memberId: Long, targetId: Long): List<Long> {
        return when (searchLocation) {
            SPACE -> {
                val space = permissionValidator.validateSpaceByMemberId(memberId, targetId)
                containerRepository.findBySpace(space).map { it.id }
            }
            CONTAINER -> {
                val container = permissionValidator.validateContainerByMemberId(memberId = memberId, containerId = targetId)
                listOf(container.id)
            }
        }
    }

    @Transactional
    fun deleteItem(itemId: Long, memberId: Long) {
        val item = findMemberItemOrThrowException(itemId, memberId)
        itemRepository.delete(item)
    }

    @Transactional
    fun updateItem(itemId: Long, memberId: Long, request: UpdateItemRequest): ItemDetailResponse {
        val item = findMemberItemOrThrowException(itemId, memberId)
        item.updateItem(
            container = containerRepository.findWithSpaceByIdAndMemberId(request.containerId, memberId)
                ?: throw BadRequestException(message = "존재하지 않는 보관함입니다"),
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
        item.tags.removeIf { !request.tagIds.contains(it.tag.id) }
        val newTags = request.tagIds.filterNot { tagId -> item.tags.map { it.tag.id }.contains(tagId) }
        if (newTags.isNotEmpty()) {
            itemTagService.createItemTags(item, newTags, memberId)
        }
        return ItemDetailResponse(item)
    }

    private fun findMemberItemOrThrowException(itemId: Long, memberId: Long): ItemEntity {
        return itemRepository.findByIdWithContainerAndSpaceOrThrowException(itemId)
            .also {
                require(it.isValidMemberId(memberId)) {
                    throw ForbiddenException(message = "권한이 없습니다")
                }
            }
    }
}
