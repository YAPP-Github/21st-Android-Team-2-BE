package com.yapp.itemfinder.sample.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

interface SingleTableItemRepository : JpaRepository<Item, Long>

@RequestMapping("/single-table")
@RestController
class SingleTableController(
    private val singleTableRepository: SingleTableItemRepository
) {

    @GetMapping("/add")
    fun addData() {
        singleTableRepository.save(FoodSingleTable(category = "음식", name = "식품 이름", description = "식품 설명", gram = 1000, taste = "단맛"))
        singleTableRepository.save(LifeSingleTable(category = "생활용품", name = "생활 이름", description = "생활 설명", period = "기간"))
    }

    @GetMapping("/get")
    fun getData(): List<SingleTableItemDto> {
        return singleTableRepository.findAll().map { SingleTableItemDto.of(item = it) }
    }
}
