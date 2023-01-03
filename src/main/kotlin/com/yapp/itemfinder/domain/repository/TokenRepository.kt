package com.yapp.itemfinder.domain.repository

import com.yapp.itemfinder.domain.entity.token.TokenEntity
import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<TokenEntity, Long>
