package com.yapp.itemfinder.api

import com.yapp.itemfinder.common.PageResponse
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.validation.UrlValidator
import com.yapp.itemfinder.domain.item.ItemService
import com.yapp.itemfinder.domain.item.dto.ItemSearchOption
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemDueDateTarget
import com.yapp.itemfinder.domain.item.dto.ItemDetailResponse
import com.yapp.itemfinder.domain.item.dto.ItemWithDueDateResponse
import com.yapp.itemfinder.domain.item.dto.ItemOverviewResponse
import com.yapp.itemfinder.domain.item.dto.UpdateItemRequest
import com.yapp.itemfinder.domain.member.MemberEntity
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RequestMapping("/items")
@RestController
@Validated
class ItemController(
    private val itemService: ItemService,
    private val urlValidator: UrlValidator
) {
    @Operation(summary = "물건 등록")
    @PostMapping
    fun createItem(
        @LoginMember member: MemberEntity,
        @RequestBody @Valid
        createItemRequest: CreateItemRequest
    ): ItemDetailResponse {
        require(urlValidator.isValid(createItemRequest.imageUrls)) {
            throw BadRequestException(message = "url 형식이 올바르지 않습니다")
        }
        return itemService.createItem(createItemRequest, member.id)
    }

    @Operation(summary = "물건 상세 조회")
    @GetMapping("/{itemId}")
    fun getItem(
        @LoginMember member: MemberEntity,
        @PathVariable itemId: Long
    ): ItemDetailResponse {
        return itemService.findItem(itemId, member.id)
    }

    @Operation(summary = "물건 검색")
    @PostMapping("/search")
    fun searchItems(
        @LoginMember member: MemberEntity,
        @RequestParam(required = false, defaultValue = "0") @Min(0)
        page: Int,
        @RequestParam(required = false, defaultValue = "20") @Min(1) @Max(20)
        size: Int,
        @RequestBody searchOption: ItemSearchOption
    ): PageResponse<ItemOverviewResponse> {
        val pageRequest = PageRequest.of(page, size, searchOption.getSort())
        return itemService.search(searchOption, pageRequest, member.id)
    }

    @Operation(summary = "소비기한 설정한 아이템 조회")
    @GetMapping("/by-due-date")
    fun searchByDueDate(
        @LoginMember member: MemberEntity,
        @RequestParam(required = false, defaultValue = "0") @Min(0) page: Int,
        @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(10) size: Int,
        @RequestParam target: ItemDueDateTarget
    ): PageResponse<ItemWithDueDateResponse> {
        val pageRequest = PageRequest.of(page, size)
        return itemService.searchByDueDate(
            pageRequest = pageRequest,
            memberId = member.id,
            dueDateTarget = target
        )
    }

    @Operation(summary = "물건 삭제")
    @DeleteMapping("/{itemId}")
    fun deleteItem(
        @LoginMember member: MemberEntity,
        @PathVariable itemId: Long
    ) {
        itemService.deleteItem(itemId, member.id)
    }

    @Operation(summary = "물건 수정")
    @PutMapping("/{itemId}")
    fun updateItem(
        @LoginMember member: MemberEntity,
        @PathVariable itemId: Long,
        @RequestBody @Valid updateItemRequest: UpdateItemRequest
    ): ItemDetailResponse {
        require(urlValidator.isValid(updateItemRequest.imageUrls)) {
            throw BadRequestException(message = "url 형식이 올바르지 않습니다")
        }
        return itemService.updateItem(itemId, member.id, updateItemRequest)
    }
}
