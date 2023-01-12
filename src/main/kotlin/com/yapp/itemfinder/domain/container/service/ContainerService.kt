package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.SpaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ContainerService(
    private val containerRepository: ContainerRepository,
    private val spaceRepository: SpaceRepository
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
        val space = spaceRepository.findByIdAndMemberId(id = spaceId, memberId = requestMemberId)
            ?: throw BadRequestException(message = "해당 유저가 등록한 공간이 없습니다")

        return containerRepository.findBySpaceOrderByCreatedAtAsc(space)
            .map { ContainerResponse(it) }
    }
}
