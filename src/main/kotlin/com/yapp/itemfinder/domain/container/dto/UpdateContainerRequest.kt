package com.yapp.itemfinder.domain.container.dto

import com.yapp.itemfinder.api.validation.EnumType
import com.yapp.itemfinder.domain.container.ContainerEntity.Companion.CONTAINER_NAME_LENGTH_LIMIT
import com.yapp.itemfinder.domain.container.IconType
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class UpdateContainerRequest(
    val spaceId: Long,

    @field:NotBlank
    @field:Size(min = 1, max = CONTAINER_NAME_LENGTH_LIMIT, message = "1자 이상 ${CONTAINER_NAME_LENGTH_LIMIT}자 이내로 이름을 등록해 주세요.")
    val name: String,

    @EnumType(enumClass = IconType::class, message = "올바르지 않은 아이콘입니다")
    @Schema(implementation = IconType::class)
    val icon: String,

    val url: String? = null,
)
