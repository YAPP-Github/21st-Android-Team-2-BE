package com.yapp.itemfinder.domain.item.service

import com.yapp.itemfinder.api.exception.ForbiddenException
import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.domain.container.ContainerEntity
import com.yapp.itemfinder.domain.container.ContainerRepository
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.SpaceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class PermissionValidator(
    private val containerRepository: ContainerRepository,
    private val spaceRepository: SpaceRepository,
) {
    fun validateSpaceByMemberId(memberId: Long, spaceId: Long): SpaceEntity {
        val space = spaceRepository.findByIdOrNull(spaceId) ?: throw NotFoundException(message = "해당 공간을 찾을 수 없습니다")
        return space.also {
            validateCreatorIdAndRequestId(it.getCreatorId(), memberId)
        }
    }

    fun validateContainerByMemberId(memberId: Long, containerId: Long): ContainerEntity {
        val container = containerRepository.findByIdWithSpace(containerId) ?: throw NotFoundException(message = "해당 보관함을 찾을 수 없습니다")
        return container.also {
            validateCreatorIdAndRequestId(it.getCreatorId(), memberId)
        }
    }

    private fun validateCreatorIdAndRequestId(creatorMemberId: Long, requestMemberId: Long) {
        if (creatorMemberId != requestMemberId) {
            throw ForbiddenException(message = "해당 권한이 없습니다.")
        }
    }
}
