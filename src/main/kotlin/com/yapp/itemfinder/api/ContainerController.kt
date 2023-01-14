package com.yapp.itemfinder.api

import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.container.dto.CreateContainerRequest
import com.yapp.itemfinder.domain.container.service.ContainerService
import com.yapp.itemfinder.domain.member.MemberEntity
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class ContainerController(
    private val containerService: ContainerService
) {
    @Operation(summary = "특정 공간에 등록된 모든 보관함 조회")
    @GetMapping("/containers/by-space-id/{spaceId}")
    fun findContainersInSpace(@LoginMember member: MemberEntity, @PathVariable spaceId: Long): List<ContainerResponse> {
        return containerService.findContainersInSpace(member.id, spaceId)
    }

    @Operation(summary = "새로운 보관함 등록")
    @PostMapping("/containers")
    fun createContainer(@LoginMember member: MemberEntity, @RequestBody @Valid createContainerReq: CreateContainerRequest): ContainerResponse {
        return containerService.createContainer(member.id, createContainerReq)
    }
}