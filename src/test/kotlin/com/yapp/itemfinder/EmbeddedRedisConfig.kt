package com.yapp.itemfinder

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
class EmbeddedRedisConfig {
    @Value("\${spring.redis.port}")
    private var port: Int = 0
    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        this.redisServer = RedisServer(port)
        this.redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        this.redisServer.stop()
    }
}
