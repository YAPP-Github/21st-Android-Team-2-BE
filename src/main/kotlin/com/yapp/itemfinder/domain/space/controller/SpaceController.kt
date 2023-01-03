package com.yapp.itemfinder.domain.space.controller

import com.yapp.itemfinder.api.LoginMember
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.space.dto.SpacesResponse
import com.yapp.itemfinder.domain.space.service.SpaceService
import com.yapp.itemfinder.domain.member.MemberEntity
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val spaceService: SpaceService,
) {
    @Operation(summary = "새로운 공간 등록")
    @PostMapping("/spaces")
    fun createSpace(@LoginMember member: MemberEntity, @RequestBody createSpaceReq: CreateSpaceRequest) {
        spaceService.createSpace(
            spaceRequest = createSpaceReq,
            member = member
        )
    }
    @Operation(summary = "멤버가 등록한 공간 리스트 조회")
    @GetMapping("/spaces")
    fun getSpaces(@LoginMember member: MemberEntity): SpacesResponse {
        return spaceService.getSpaces(member.id)
    }
}
