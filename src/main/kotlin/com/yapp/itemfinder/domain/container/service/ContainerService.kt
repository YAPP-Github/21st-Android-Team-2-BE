package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.domain.container.ContainerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ContainerService(
    private val containerRepository: ContainerRepository
) {
    fun getSpaceIdToContainers(spaceIds: List<Long>): Map<Long, List<ContainerVo>> {
        return containerRepository.findBySpaceIdIsIn(spaceIds)
            .groupBy { it.space.id }
            .mapValues { (_, containers) ->
                containers.map { ContainerVo(it) }
            }
    }
}
