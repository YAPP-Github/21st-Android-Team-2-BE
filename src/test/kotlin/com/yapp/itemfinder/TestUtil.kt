package com.yapp.itemfinder

object TestUtil {
    fun generateRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun generateRandomPositiveLongValue(): Long {
        return (1..Long.MAX_VALUE).random()
    }
}
