package com.example.tradestatisticsaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.example.tradestatisticsaggregator.rest.TradeStatsService;
import com.example.tradestatisticsaggregator.service.RestClientCreator;
import com.example.tradestatisticsaggregator.service.impl.GenericClientFactory;

@Configuration
public class WebConfig {

	@Bean
	RestClient restClient() {
		return RestClient.create();
	}

	@Bean
	RestClientCreator<TradeStatsService> tradeStatsServiceRestClientCreator() {
		return new RestClientCreator<>(new GenericClientFactory<>(TradeStatsService.class));
	}

}
