package com.yapp.itemfinder.common

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val totalCount: Int,
    val totalPages: Int,
    val currentPageNumber: Int,
    val hasNext: Boolean,
    val data: List<T>
) {
    constructor(page: Page<T>) : this(
        totalCount = page.totalElements.toInt(),
        totalPages = page.totalPages,
        currentPageNumber = page.pageable.pageNumber,
        hasNext = page.hasNext(),
        data = page.content
    )
}
