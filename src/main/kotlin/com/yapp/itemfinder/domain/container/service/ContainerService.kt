package com.yapp.itemfinder.domain.container.service

import com.yapp.itemfinder.domain.container.ContainerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ContainerService(
    private val containerRepository: ContainerRepository
) {
    fun getSpaceIdToContainerIconNames(spaceIds: List<Long>): Map<Long, List<String>> {
        val spaceIdWithContainerIcon = containerRepository.findIconTypeBySpaceIdIsIn(spaceIds)
        val spaceIdToContainerIconNames = spaceIdWithContainerIcon.groupBy { it.spaceId }
            .mapValues { (_, spaceIdToContainerIconType) ->
                spaceIdToContainerIconType.map { it.iconType.name }
            }
        return spaceIdToContainerIconNames
    }
}
