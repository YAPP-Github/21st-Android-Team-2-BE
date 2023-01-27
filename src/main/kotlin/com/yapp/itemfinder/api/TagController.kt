package com.yapp.itemfinder.api

import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.tag.TagService
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagWithItemTypeResponse
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/tags")
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
    fun findTagsWithItemType(@LoginMember member: MemberEntity, @PageableDefault(page = 0, size = 10) pageable: Pageable): PageResponse<TagWithItemTypeResponse> {
        return tagService.findTagWithItemType(member, PageRequest.of(pageable.pageNumber, pageable.pageSize))
    }
}
