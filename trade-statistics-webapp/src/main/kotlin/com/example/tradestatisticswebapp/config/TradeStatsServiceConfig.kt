package com.example.tradestatisticswebapp.config

import com.example.tradestatisticswebapp.stats.TradeStatsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class TradeStatsServiceConfig {

    @Value("\${trade-statistics.base-url}")
    lateinit var tradeStatisticsServiceBaseURL: String

    @Bean
    fun tradeStatsService(): TradeStatsService {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(RestClient.create(tradeStatisticsServiceBaseURL))).build()
            .createClient(TradeStatsService::class.java)
    }

}