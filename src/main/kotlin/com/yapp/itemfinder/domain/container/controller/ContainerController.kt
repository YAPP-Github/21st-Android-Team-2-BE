package com.yapp.itemfinder.domain.container.controller

import com.yapp.itemfinder.api.LoginMember
import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.container.service.ContainerService
import com.yapp.itemfinder.domain.member.MemberEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ContainerController(
    private val containerService: ContainerService
) {
    @GetMapping("/containers/by-space-id/{spaceId}")
    fun findContainersInSpace(@LoginMember member: MemberEntity, @PathVariable spaceId: Long): List<ContainerResponse> {
        return containerService.findContainersInSpace(member.id, spaceId)
    }
}
