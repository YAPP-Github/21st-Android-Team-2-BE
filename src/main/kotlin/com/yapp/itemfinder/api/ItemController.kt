package com.yapp.itemfinder.api

import com.yapp.itemfinder.domain.item.ItemService
import com.yapp.itemfinder.domain.item.dto.CreateItemRequest
import com.yapp.itemfinder.domain.item.dto.ItemResponse
import com.yapp.itemfinder.domain.member.MemberEntity
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/items")
@RestController
class ItemController(
    private val itemService: ItemService
) {
    @Operation(summary = "물건 등록")
    @PostMapping
    fun createItem(
        @LoginMember member: MemberEntity,
        @RequestBody @Valid createItemRequest: CreateItemRequest
    ): ItemResponse {
        return itemService.createItem(createItemRequest, member.id)
    }
}
