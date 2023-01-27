package com.yapp.itemfinder.api.validation

import com.yapp.itemfinder.common.Const
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.regex.Pattern

interface UrlValidator {
    companion object {
        val URL_PATTERN = Pattern.compile(Const.URL_REGEX)
    }
    fun isValid(url: String): Boolean
    fun isValid(urls: List<String>): Boolean {
        return urls.all { isValid(it) }
    }
}

@Profile("dev", "prod")
@Component
class ImageUrlValidator(
    @Value("\${cloud.aws.s3.protocol}")
    private val protocol: String,
    @Value("\${cloud.aws.s3.host}")
    private val host: String
) : UrlValidator {
    override fun isValid(url: String): Boolean {
        return UrlValidator.URL_PATTERN.matcher(url).matches() && isValidFileStorage(url)
    }
    private fun isValidFileStorage(url: String): Boolean {
        val (scheme, remainder) = url.split("://")
        return (scheme == protocol) && (remainder.split("/")[0] == host)
    }
}

@Profile("!dev", "!prod")
@Component
class SimpleUrlValidator : UrlValidator {
    override fun isValid(url: String): Boolean {
        return UrlValidator.URL_PATTERN.matcher(url).matches()
    }
}
