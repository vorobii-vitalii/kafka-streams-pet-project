package com.example.tradestatisticswebapp.stats

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange(url = "trade-stats", accept = [MediaType.APPLICATION_JSON_VALUE])
interface TradeStatsService {

    @GetExchange("/{symbol}")
    fun getNumberOfTrades(@PathVariable symbol: String?): ResponseEntity<Long?>

}
