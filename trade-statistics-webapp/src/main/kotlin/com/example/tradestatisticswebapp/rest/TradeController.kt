package com.example.tradestatisticswebapp.rest

import com.example.tradestatisticswebapp.dto.CreateTrade
import com.example.tradestatisticswebapp.stats.TradeAdder
import com.example.tradestatisticswebapp.stats.TradeStatsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import trade.api.Trade

@RestController
@RequestMapping("/trades")
class TradeController(val tradeAdder: TradeAdder, val tradeStatsService: TradeStatsService) {
    val logger: Logger = LoggerFactory.getLogger(TradeController::class.java)

    @PostMapping
    fun addTrade(@RequestBody createTrade: CreateTrade): ResponseEntity<Any> {
        logger.info("Adding trade {}", createTrade)
        tradeAdder.addTrade(
            Trade.newBuilder()
                .setQuantity(1)
                .setSymbol(createTrade.symbol)
                .setUserId(createTrade.userId)
                .build()
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{symbol}")
    fun getTradeStats(@PathVariable("symbol") symbol: String): ResponseEntity<Long> {
        logger.info("Getting trade stats for {}", symbol)
        return ResponseEntity.ok(tradeStatsService.getNumberOfTrades(symbol).body)
    }


}
