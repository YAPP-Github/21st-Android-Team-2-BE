package com.yapp.itemfinder.domain.item.dto

import com.yapp.itemfinder.domain.item.ItemType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class SearchOption(
    val sortOrderOption: SortOrderOption = SortOrderOption.NameAsc,
    val itemTypes: List<ItemType> = emptyList(),
    val tagNames: List<String> = emptyList(),
    val itemName: String? = null,
    val searchTarget: SearchTarget? = null
) {
    fun toPageRequest(page: Int = 0, size: Int = 20): PageRequest {
        return PageRequest.of(page, size, sortOrderOption.toSort())
    }

    fun getSort(): Sort {
        return sortOrderOption.toSort()
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
