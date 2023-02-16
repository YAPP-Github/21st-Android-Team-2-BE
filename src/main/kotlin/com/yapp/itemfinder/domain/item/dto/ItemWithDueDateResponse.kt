package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.api.exception.InternalServerException
import com.yapp.itemfinder.common.Const.KST_ZONE_ID
import com.yapp.itemfinder.common.DateTimeFormatter.YYYYMMDD
import com.yapp.itemfinder.domain.item.ItemEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ItemWithDueDateResponse(
    val id: Long,
    val name: String,
    val itemType: String,
    val useByDate: String,
    val remainDate: Long
) {

    companion object {
        fun from(item: ItemEntity): ItemWithDueDateResponse {
            val dueDate: LocalDateTime = item.dueDate ?: throw InternalServerException(message = "해당 아이템은 소비기한이 존재하지 않습니다. id: ${item.id}")
            val today = LocalDate.now(KST_ZONE_ID)
            val remainDueDate = ChronoUnit.DAYS.between(today, dueDate.toLocalDate())

            return ItemWithDueDateResponse(
                id = item.id,
                name = item.name,
                itemType = item.type.name,
                useByDate = dueDate.format(YYYYMMDD),
                remainDate = remainDueDate
            )
        }
    }
}
