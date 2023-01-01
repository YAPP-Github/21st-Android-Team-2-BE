package com.yapp.itemfinder.domain.entity.space.controller

import com.yapp.itemfinder.api.auth.LoginMember
import com.yapp.itemfinder.domain.entity.member.MemberEntity
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.entity.space.dto.SpacesResponse
import com.yapp.itemfinder.domain.entity.space.service.SpaceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val spaceService: SpaceService,
) {
    @PostMapping("/spaces")
    fun createSpace(@LoginMember member: MemberEntity, @RequestBody createSpaceReq: CreateSpaceRequest) {
        spaceService.createSpace(
            spaceRequest = createSpaceReq,
            member = member
        )
    }

    @GetMapping("/spaces")
    fun getSpaces(@LoginMember member: MemberEntity): SpacesResponse {
        return spaceService.getSpaces(member.id)
    }
}
