package com.yapp.itemfinder.domain.container.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.CONTAINER_NAME_LENGTH_LIMIT
import com.yapp.itemfinder.domain.container.IconType
import org.hibernate.validator.constraints.URL
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class CreateContainerRequest(
    val spaceId: Long,
    @field:NotBlank
    @field:Size(min = 1, max = CONTAINER_NAME_LENGTH_LIMIT, message = "1자 이상 ${CONTAINER_NAME_LENGTH_LIMIT}자 이내로 이름을 등록해 주세요.")
    val name: String,
    @JsonProperty(value = "icon")
    private val _icon: String,
    @field:URL(message = "올바른 URL 형식이어야 합니다")
    val url: String? = null,
) {
    val icon: IconType by lazy {
        IconType.values().firstOrNull { it.name == _icon } ?: throw BadRequestException(message = "올바르지 않은 아이콘입니다")
    }
}
