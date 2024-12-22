package com.example.tradestatisticswebapp.rest

import com.example.tradestatisticswebapp.dto.CreateUser
import com.example.tradestatisticswebapp.users.UserAdder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import trade.api.User

@RestController
@RequestMapping("/users")
class UserController(val userAdder: UserAdder) {
    val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping
    fun create(@RequestBody createUser: CreateUser): ResponseEntity<Any> {
        logger.info("Creating user {}", createUser)
        userAdder.addUser(
            User.newBuilder()
                .setAddressCountry(createUser.location)
                .setUserId(createUser.userId)
                .build()
        )
        return ResponseEntity.ok().build()
    }

}