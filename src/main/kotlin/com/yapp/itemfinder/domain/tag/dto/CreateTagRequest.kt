package com.yapp.itemfinder.domain.tag.dto

import org.hibernate.validator.constraints.Length
import javax.validation.Valid

data class CreateTagRequest(
    @field:Length(min = 1, max = 10)
    val name: String
)

data class CreateTagsRequest(
    @field:Valid
    val tags: List<CreateTagRequest>
)
