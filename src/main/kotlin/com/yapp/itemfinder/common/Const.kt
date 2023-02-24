package com.yapp.itemfinder.common

import java.time.ZoneId

object Const {
    val KST_ZONE_ID = ZoneId.of("Asia/Seoul")
    const val BEARER = "Bearer"
    enum class ImageFormat {
        JPEG, JPG, PNG, GIF
    }
    const val URL_REGEX = "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$"
}
