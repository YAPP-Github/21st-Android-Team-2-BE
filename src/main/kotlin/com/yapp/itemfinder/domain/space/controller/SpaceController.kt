package com.yapp.itemfinder.domain.space.controller

import com.yapp.itemfinder.api.LoginMember
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.space.dto.SpacesResponse
import com.yapp.itemfinder.domain.space.service.SpaceService
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.space.dto.SpaceResponse
import com.yapp.itemfinder.domain.space.dto.SpaceWithTopContainerResponse
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
    fun createSpace(@LoginMember member: MemberEntity, @RequestBody createSpaceReq: CreateSpaceRequest): SpaceResponse {
        return spaceService.createSpace(
            spaceRequest = createSpaceReq,
            member = member
        )
    }
    @Operation(summary = "멤버가 등록한 공간 리스트 조회")
    @GetMapping("/spaces")
    fun getSpaces(@LoginMember member: MemberEntity): SpacesResponse {
        return spaceService.getSpaces(member.id)
    }

    @Operation(summary = "멤버가 등록한 공간들에 대한 보관함 개수 및 보관함 정보 리스트 조회")
    @GetMapping("/spaces/containers")
    fun getSpaceWithTopContainers(@LoginMember member: MemberEntity): List<SpaceWithTopContainerResponse> {
        return spaceService.getSpaceWithTopContainers(member.id)
    }
}
