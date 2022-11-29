package com.yapp.itemfinder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class ItemFinderApplication

fun main(args: Array<String>) {
	runApplication<ItemFinderApplication>(*args)
}
