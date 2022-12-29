package com.yapp.itemfinder.domain.service

import com.yapp.itemfinder.api.exception.BadRequestException
import com.yapp.itemfinder.api.exception.NotFoundException
import com.yapp.itemfinder.domain.entity.SampleUser
import com.yapp.itemfinder.domain.repository.SampleUserRepository
import com.yapp.itemfinder.domain.service.dto.CreateUserReq
import com.yapp.itemfinder.domain.service.dto.CreateUserRes
import com.yapp.itemfinder.domain.service.dto.GetUserRes
import com.yapp.itemfinder.domain.service.dto.UpdateUserReq
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class SampleUserService(
    private val userRepository: SampleUserRepository
) {

    @Transactional
    fun insertUser(dto: CreateUserReq): CreateUserRes {
        if (userRepository.existsByEmail(dto.email)) {
            throw BadRequestException(message = "이미 존재하는 회원입니다.")
        }
        return CreateUserRes(userRepository.save(dto.toEntity()).id)
    }

    fun findUser(userId: Long): GetUserRes {
        return GetUserRes(findByIdOrException(userId))
    }

    @Transactional
    fun updateUser(userId: Long, dto: UpdateUserReq) {
        val user = findByIdOrException(userId)
        user.update(name = dto.name, email = dto.email)
    }

    @Transactional
    fun deleteUser(userId: Long) {
        userRepository.delete(findByIdOrException(userId))
    }

    fun findByIdOrException(userId: Long): SampleUser = userRepository.findByIdOrNull(userId)
        ?: throw NotFoundException(message = "잘못된 회원 id 입니다.")
}
