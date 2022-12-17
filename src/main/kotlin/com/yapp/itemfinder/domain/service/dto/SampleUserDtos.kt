package com.yapp.itemfinder.domain.service.dto

import com.yapp.itemfinder.domain.entity.SampleUser
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

data class CreateUserReq(
    @field:Pattern(regexp = "[가-힣]{1,30}", message = "올바른 형식의 이름이어야 합니다")
    val name: String,
    @field:Email(message = "올바른 형식의 이메일이어야 합니다")
    val email: String,
) {
    fun toEntity() = SampleUser(name, email)
}

data class CreateUserRes(
    val id: Long,
)

data class GetUserRes(
    val name: String,
    val email: String,
) {
    constructor(sampleUser: SampleUser) : this(sampleUser.name, sampleUser.email)
}


data class UpdateUserReq(
    val name: String?,
    val email: String?
)
