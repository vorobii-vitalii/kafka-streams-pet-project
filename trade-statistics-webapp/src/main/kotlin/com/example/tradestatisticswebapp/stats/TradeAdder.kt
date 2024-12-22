package com.example.tradestatisticswebapp.stats

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import trade.api.Trade

@Service
class TradeAdder(val producer: KafkaTemplate<Int, Trade>) {

    fun addTrade(trade: Trade) {
        producer.send(ProducerRecord("trades", trade))
    }

}
