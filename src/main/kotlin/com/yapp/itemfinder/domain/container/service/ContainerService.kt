package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.container.dto.CreateContainerRequest
import com.yapp.itemfinder.domain.container.dto.UpdateContainerRequest
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.space.findByIdAndMemberIdOrThrowException
import com.yapp.itemfinder.support.PermissionValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ContainerService(
    private val containerRepository: ContainerRepository,
    private val spaceRepository: SpaceRepository,
    private val permissionValidator: PermissionValidator
) {
    fun getSpaceIdToContainers(spaceIds: List<Long>): Map<Long, List<ContainerVo>> {
        return containerRepository.findBySpaceIdIsIn(spaceIds)
            .groupBy { it.space.id }
            .mapValues { (_, containers) ->
                containers.map { ContainerVo(it) }
            }
    }

    fun addDefaultContainer(newSpace: SpaceEntity) {
        val defaultContainer = ContainerEntity(space = newSpace)
        containerRepository.save(defaultContainer)
    }

    fun findContainersInSpace(requestMemberId: Long, spaceId: Long): List<ContainerResponse> {
        val space = spaceRepository.findByIdAndMemberIdOrThrowException(id = spaceId, memberId = requestMemberId)

        return containerRepository.findBySpaceOrderByCreatedAtAsc(space)
            .map { ContainerResponse(it) }
    }

    @Transactional
    fun createContainer(memberId: Long, containerRequest: CreateContainerRequest): ContainerResponse {
        val space = spaceRepository.findByIdAndMemberIdOrThrowException(id = containerRequest.spaceId, memberId = memberId).also {
            validateContainerExist(spaceId = it.id, containerName = containerRequest.name)
        }

        val container = ContainerEntity(
            space = space,
            name = containerRequest.name,
            iconType = containerRequest.icon,
            imageUrl = containerRequest.url
        )

        return containerRepository.save(container).run {
            ContainerResponse(this)
        }
    }

    @Transactional
    fun updateContainer(memberId: Long, containerId: Long, updateContainerRequest: UpdateContainerRequest): ContainerResponse {
        val existContainer = permissionValidator.validateContainerByMemberId(memberId, containerId)
        val (name, icon, url) = Triple(updateContainerRequest.name, updateContainerRequest.icon, updateContainerRequest.url)

        val space = if (existContainer.space.id != updateContainerRequest.spaceId) {
            permissionValidator.validateSpaceByMemberId(memberId = memberId, spaceId = updateContainerRequest.spaceId)
        } else {
            existContainer.space
        }

        return existContainer.update(
            space = space,
            name = name,
            iconType = icon,
            imageUrl = url
        ).run {
            ContainerResponse(this)
        }
    }

    private fun validateContainerExist(spaceId: Long, containerName: String) {
        containerRepository.findBySpaceIdAndName(spaceId = spaceId, name = containerName)?.let {
            throw ConflictException(message = "이미 해당 이름으로 공간에 등록된 보관함 존재합니다.")
        }
    }
}
