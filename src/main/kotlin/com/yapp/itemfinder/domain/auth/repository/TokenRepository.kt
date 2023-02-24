package com.yapp.itemfinder.domain.auth.repository

import com.yapp.itemfinder.domain.token.TokenEntity
import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<TokenEntity, Long>
