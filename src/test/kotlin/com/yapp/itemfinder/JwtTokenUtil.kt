package com.yapp.itemfinder

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date

object JwtTokenUtil {

    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode("5dc5ef5de6e3094ec5fd308585eeff44950e9d8b87e95044bcbf7ec7200fd968632d73ee605c07df2a9d1f7dd6e5ced6903f9d029f682464079d311daeebb339"))

    fun createToken(subject: String, expireMillis: Long): String {
        val now = Date()
        val expiration = Date(now.time + expireMillis)
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
}
