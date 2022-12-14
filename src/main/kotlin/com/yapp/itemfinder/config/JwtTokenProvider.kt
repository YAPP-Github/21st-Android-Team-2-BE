package com.yapp.itemfinder.config

import com.yapp.itemfinder.api.exception.INVALID_TOKEN_MESSAGE
import com.yapp.itemfinder.api.exception.UnauthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    val accessTokenExpirationMilliseconds: Long = 1000L * 60 * 60 * 12 // 12시간
    val refreshTokenExpirationMilliseconds: Long = 1000L * 60 * 60 * 24 * 30 // 30일

    fun createAccessToken(subject: String): String {
        return createToken(subject, accessTokenExpirationMilliseconds)
    }

    fun createRefreshToken(subject: String): String {
        return createToken(subject, refreshTokenExpirationMilliseconds)
    }

    private fun createToken(subject: String, expireMillis: Long): String {
        val now = Date()
        val expiration = Date(now.time + expireMillis)
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getSubject(token: String): String {
        return getClaims(token).subject
    }

    private fun getClaims(token: String): Claims {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            throw UnauthorizedException(message = INVALID_TOKEN_MESSAGE)
        }
    }
}
