package com.example.tradestatisticswebapp.users

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import trade.api.User

@Service
class UserAdder(val producer: KafkaTemplate<Int, User>) {

    fun addUser(user: User) {
        producer.send(ProducerRecord("users", user))
    }

}
