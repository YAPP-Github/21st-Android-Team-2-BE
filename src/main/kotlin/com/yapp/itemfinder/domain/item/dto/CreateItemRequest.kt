package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.api.validation.EnumType
import com.yapp.itemfinder.domain.item.ItemType
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.URL
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.Future
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.PastOrPresent

data class CreateItemRequest(
    @Schema(description = "보관함 아이디")
    val containerId: Long,

    @Schema(description = "아이템 이름")
    val name: String,

    @EnumType(enumClass = ItemType::class, message = "올바르지 않은 카테고리입니다")
    @Schema(implementation = ItemType::class)
    val category: String,

    @field:Min(1)
    @field:Max(99)
    @Schema(description = "개수")
    val quantity: Int,

    @field:Valid
    @Schema(description = "이미지 url")
    val imageUrls: List<Image> = listOf(),

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
    val pinY: Float? = null
)

data class Image(
    @field:URL
    val url: String
)
