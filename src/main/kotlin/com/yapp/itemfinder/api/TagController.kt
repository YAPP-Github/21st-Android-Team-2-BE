package com.yapp.itemfinder.api

import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.tag.TagService
import com.yapp.itemfinder.domain.tag.dto.CreateTagsRequest
import com.yapp.itemfinder.domain.tag.dto.TagsResponse
import io.swagger.v3.oas.annotations.Operation
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
    fun createTag(@LoginMember member: MemberEntity, @RequestBody @Valid createTagRequest: CreateTagsRequest): TagsResponse {
        return tagService.findOrCreateTags(member, createTagRequest)
    }
}
