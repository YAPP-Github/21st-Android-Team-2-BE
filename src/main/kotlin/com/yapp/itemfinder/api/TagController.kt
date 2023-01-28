package com.yapp.itemfinder.api

import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.tag.TagService
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeResponse
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@RequestMapping("/tags")
@Validated
class TagController(
    private val tagService: TagService
) {
    @Operation(summary = "태그 등록")
    @PostMapping
    fun createTags(@LoginMember member: MemberEntity, @RequestBody @Valid createTagRequest: CreateTagsRequest): TagsResponse {
        return tagService.findOrCreateTags(member, createTagRequest)
    }

    @Operation(summary = "태그 전체 조회")
    @GetMapping
    fun findTags(@LoginMember member: MemberEntity): TagsResponse {
        return tagService.findTags(member)
    }

    @Operation(summary = "태그 & 물건 타입별 개수 조회")
    @GetMapping("/detail")
    fun findTagsWithItemType(
        @LoginMember member: MemberEntity,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(10) size: Int
    ): PageResponse<TagWithItemTypeResponse> {
        return tagService.findTagWithItemType(member, PageRequest.of(page, size))
    }
}
