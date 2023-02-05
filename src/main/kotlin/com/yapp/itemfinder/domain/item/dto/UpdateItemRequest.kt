package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.api.validation.EnumType
import com.yapp.itemfinder.domain.item.ItemType
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.Future
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

data class UpdateItemRequest(
    @Schema(description = "보관함 아이디")
    val containerId: Long,

    @Schema(description = "아이템 이름")
    val name: String,

    @EnumType(enumClass = ItemType::class, message = "올바르지 않은 카테고리입니다")
    @Schema(implementation = ItemType::class)
    val itemType: String,

    @field:Min(0)
    @field:Max(99)
    @Schema(description = "개수")
    val quantity: Int,

    @Schema(description = "이미지 url")
    @field:Size(max = 10)
    val imageUrls: List<String> = listOf(),

    @Schema(description = "태그 아이디")
    val tagIds: List<Long> = listOf(),

    @field:Length(max = 200)
    @Schema(description = "메모")
    val description: String? = null,

    @field:PastOrPresent
    @Schema(description = "구매일")
    val purchaseDate: LocalDate? = null,

    @field:Future
    @Schema(description = "소비기한")
    val useByDate: LocalDateTime? = null,

    @field:Min(0)
    @field:Max(100)
    @Schema(description = "핀 width 위치(%)")
    val pinX: Float? = null,

    @field:Min(0)
    @field:Max(100)
    @Schema(description = "핀 height 위치(%)")
    val pinY: Float? = null,
)
