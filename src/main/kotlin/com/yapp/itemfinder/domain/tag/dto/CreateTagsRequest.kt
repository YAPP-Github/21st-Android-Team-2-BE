package com.yapp.itemfinder.domain.tag.dto

import com.yapp.itemfinder.api.exception.BadRequestException

data class CreateTagsRequest(
    val tags: List<String>
) {
    init {
        require(tags.all { validateLength(it) }) {
            throw BadRequestException(message = "태그 이름은 길이가 1 이상 10 이하여야 합니다")
        }
    }

    private fun validateLength(name: String): Boolean {
        return name.length in 1..10
    }
}
