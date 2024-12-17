package com.example.tradestatisticsaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

	@Bean
	public KafkaStreamsInteractiveQueryService kafkaStreamsInteractiveQueryService(StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
		return new KafkaStreamsInteractiveQueryService(streamsBuilderFactoryBean);
	}

}

