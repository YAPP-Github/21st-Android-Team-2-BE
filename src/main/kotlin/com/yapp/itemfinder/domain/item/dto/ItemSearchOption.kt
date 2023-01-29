package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.domain.item.ItemType
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.data.domain.Sort

data class ItemSearchOption(
    val sortOrderOption: SortOrderOption = SortOrderOption.RecentCreated,
    val itemTypes: List<ItemType> = emptyList(),
    val tagNames: List<String> = emptyList(),
    val itemName: String? = null,
    val searchTarget: SearchTarget? = null
) {
    @Hidden
    fun getSort(): Sort {
        return sortOrderOption.toSort()
    }

    enum class SortOrderOption(
        private val targetField: String,
        private val sortOrder: Sort.Direction
    ) {
        RecentCreated("createdAt", Sort.Direction.DESC),
        PastCreated("createdAt", Sort.Direction.ASC),
        NameAsc("name", Sort.Direction.ASC),
        NameDesc("name", Sort.Direction.DESC);

        fun toSort(): Sort {
            return Sort.by(sortOrder, targetField)
        }
    }

    data class SearchTarget(
        val location: SearchLocation,
        val id: Long
    ) {
        enum class SearchLocation {
            SPACE, CONTAINER
        }
    }
}
