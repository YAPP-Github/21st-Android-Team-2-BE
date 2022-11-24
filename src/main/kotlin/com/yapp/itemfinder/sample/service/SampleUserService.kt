package com.yapp.itemfinder.sample.service

import com.yapp.itemfinder.sample.entity.SampleUser
import com.yapp.itemfinder.sample.repository.SampleUserRepository
import com.yapp.itemfinder.sample.service.dto.CreateUserReq
import com.yapp.itemfinder.sample.service.dto.CreateUserRes
import com.yapp.itemfinder.sample.service.dto.GetUserRes
import com.yapp.itemfinder.sample.service.dto.UpdateUserReq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class SampleUserService(
    private val userRepository: SampleUserRepository
) {

    @Transactional
    fun insertUser(dto: CreateUserReq): CreateUserRes {
        require(!userRepository.existsByEmail(dto.email)) {IllegalArgumentException("이미 존재하는 회원입니다.")}
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

    fun findByIdOrException(userId: Long): SampleUser = userRepository.findById(userId)
        .orElseThrow() { IllegalArgumentException("잘못된 회원 id 입니다.") }

}
