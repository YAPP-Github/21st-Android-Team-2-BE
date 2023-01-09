package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.space.SpaceEntity
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

    fun addDefaultContainer(newSpace: SpaceEntity) {
        val defaultContainer = ContainerEntity(space = newSpace)
        containerRepository.save(defaultContainer)
    }
}
