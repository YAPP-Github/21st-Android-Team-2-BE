package com.yapp.itemfinder.sample.service.dto

import com.yapp.itemfinder.sample.entity.SampleUser

data class CreateUserReq(
    val name: String,
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
