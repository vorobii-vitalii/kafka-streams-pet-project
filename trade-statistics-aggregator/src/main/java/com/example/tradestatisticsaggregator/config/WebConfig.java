package com.example.tradestatisticsaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {

	@Bean
	RestClient restClient() {
		return RestClient.create();
	}

}
