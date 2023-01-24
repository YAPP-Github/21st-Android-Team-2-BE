package com.yapp.itemfinder.domain.item

import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<ItemEntity, Long>
