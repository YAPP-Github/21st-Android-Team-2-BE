package com.yapp.itemfinder.domain.entity.token

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash(value = "token")
class TokenEntity(
    @Id val memberId: Long = 0L,
    refreshToken: String = "",
    timeToLive: Long = 0L
) {

    var refreshToken: String = refreshToken
        protected set

    @TimeToLive
    var timeToLive: Long = timeToLive
        protected set
}
