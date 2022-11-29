package com.yapp.itemfinder.sample.controller

import com.yapp.itemfinder.sample.service.SampleUserService
import com.yapp.itemfinder.sample.service.dto.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/sample-user")
@RestController
class SampleUserController(
    private val userService: SampleUserService
) {

    @PostMapping()
    fun createUser(@RequestBody @Valid dto: CreateUserReq): CreateUserRes {
        return userService.insertUser(dto)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): GetUserRes {
        return userService.findUser(id)
    }

    @PatchMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody dto: UpdateUserReq) {
        return userService.updateUser(id, dto)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long) {
        userService.deleteUser(id)
    }
}
