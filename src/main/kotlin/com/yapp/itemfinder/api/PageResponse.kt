package com.yapp.itemfinder.api

/**
 * 임시 페이지네이션 응답 클래스
 * 이후에 PageResponse 추가되면 수정할 예정!!
 */
data class PageResponse<T>(
    val totalCount: Int,
    val totalPages: Int,
    val currentPageNumber: Int,
    val hasNext: Boolean,
    val data: List<T>
)
