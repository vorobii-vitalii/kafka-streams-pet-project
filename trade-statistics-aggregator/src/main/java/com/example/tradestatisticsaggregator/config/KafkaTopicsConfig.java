package com.example.tradestatisticsaggregator.config;

import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.Topics;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import trade.api.Trade;

@Configuration
public class KafkaTopicsConfig {

	@Value("${schema.registry.url}")
	private String schemaRegistryUrl;

	@Bean
	Topic<Long, Trade> tradeTopic() {
		return new Topic<>(Topics.TRADES, Serdes.Long(), createAvroSerde(false));
	}

	@Bean
	Topic<String, Long> symbolTrades() {
		return new Topic<>(Topics.SYMBOL_TRADES, Serdes.String(), Serdes.Long());
	}

	private <T extends SpecificRecord> Serde<T> createAvroSerde(boolean isKeyType) {
		Serde<T> serde = new SpecificAvroSerde<>();
		serde.configure(Map.of("schema.registry.url", schemaRegistryUrl), isKeyType);
		return serde;
	}

}
