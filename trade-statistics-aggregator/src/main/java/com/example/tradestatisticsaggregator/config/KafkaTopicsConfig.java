package com.example.tradestatisticsaggregator.config;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.tradestatisticsaggregator.avro.SerdeCreator;
import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.Topics;

import trade.api.Trade;
import trade.api.User;
import trade.api.UserTrade;

@Configuration
public class KafkaTopicsConfig {

	@Bean
	Topic<Long, Trade> tradeTopic(SerdeCreator serdeCreator) {
		return new Topic<>(Topics.TRADES, Serdes.Long(), serdeCreator.createSerde(false));
	}

	@Bean
	Topic<String, Long> symbolTrades() {
		return new Topic<>(Topics.SYMBOL_TRADES, Serdes.String(), Serdes.Long());
	}

	@Bean
	Topic<Long, User> usersTopic(SerdeCreator serdeCreator) {
		return new Topic<>(Topics.USERS, Serdes.Long(), serdeCreator.createSerde(false));
	}

	@Bean
	Topic<Integer, UserTrade> userTradeTopic(SerdeCreator serdeCreator) {
		return new Topic<>(Topics.USER_TRADES, Serdes.Integer(), serdeCreator.createSerde(false));
	}

}
