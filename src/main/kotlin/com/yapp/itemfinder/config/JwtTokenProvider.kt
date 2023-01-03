package com.yapp.itemfinder.config

import com.yapp.itemfinder.api.exception.UnauthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
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
    val accessTokenExpirationMilliseconds: Long = 1000 * 60 * 60 * 12 // 12시간
    val refreshTokenExpirationMilliseconds: Long = 1000 * 60 * 60 * 24 * 14 // 14일

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
        } catch (e: ExpiredJwtException) {
            throw UnauthorizedException(message = "만료된 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException(message = "토큰이 없습니다.")
        } catch (e: UnsupportedJwtException) {
            throw UnauthorizedException(message = "잘못된 형식의 토큰입니다.")
        } catch (e: MalformedJwtException) {
            throw UnauthorizedException(message = "잘못된 형식의 토큰입니다.")
        } catch (e: SignatureException) {
            throw UnauthorizedException(message = "잘못된 형식의 토큰입니다.")
        }
    }
}
