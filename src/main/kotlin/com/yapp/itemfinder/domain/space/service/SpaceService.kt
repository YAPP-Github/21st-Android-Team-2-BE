package com.yapp.itemfinder.domain.space.service

import com.yapp.itemfinder.api.exception.ConflictException
import com.yapp.itemfinder.domain.container.dto.ContainerResponse
import com.yapp.itemfinder.domain.container.service.ContainerService
import com.yapp.itemfinder.domain.space.SpaceRepository
import com.yapp.itemfinder.domain.entity.space.dto.CreateSpaceRequest
import com.yapp.itemfinder.domain.member.MemberEntity
import com.yapp.itemfinder.domain.space.SpaceEntity
import com.yapp.itemfinder.domain.space.dto.SpaceResponse
import com.yapp.itemfinder.domain.space.dto.SpaceWithTopContainerResponse
import com.yapp.itemfinder.domain.space.dto.SpacesResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class SpaceService(
    private val spaceRepository: SpaceRepository,
    private val containerService: ContainerService
) {
    @Transactional
    fun createSpace(spaceRequest: CreateSpaceRequest, member: MemberEntity): SpaceResponse {
        val spaceName = spaceRequest.name
        validateSpaceExist(member.id, spaceName)

        val newSpace = spaceRepository.save(SpaceEntity(member = member, name = spaceName))
        return SpaceResponse(newSpace).also {
            containerService.addDefaultContainer(newSpace)
        }
    }

    fun getSpaces(memberId: Long): SpacesResponse {
        val spaces = spaceRepository.findByMemberId(memberId)
        return SpacesResponse.from(spaces)
    }
    fun getSpaceWithTopContainers(memberId: Long): List<SpaceWithTopContainerResponse> {
        val containerIconViewLimit = 4
        val spaceWithContainerCount = spaceRepository.getSpaceWithContainerCountByMemberId(memberId)
        val spaceIdToContainers = containerService.getSpaceIdToContainers(spaceIds = spaceWithContainerCount.map { it.spaceId })

        return spaceWithContainerCount.map { spaceWithCount ->
            val containersInSpace = spaceIdToContainers.getOrDefault(spaceWithCount.spaceId, emptyList())
            SpaceWithTopContainerResponse(
                spaceWithCount.spaceId,
                spaceWithCount.spaceName,
                spaceWithCount.containerCount,
                containersInSpace
                    .take(containerIconViewLimit)
                    .map { ContainerResponse(it) }
            )
        }
    }

    private fun validateSpaceExist(memberId: Long, spaceName: String) {
        spaceRepository.findByMemberIdAndName(memberId = memberId, name = spaceName)?.let {
            throw ConflictException(message = "?????? ?????? ???????????? ????????? ????????? ???????????????.")
        }
    }
}
