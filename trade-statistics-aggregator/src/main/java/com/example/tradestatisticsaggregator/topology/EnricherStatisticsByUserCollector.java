package com.example.tradestatisticsaggregator.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trade.api.Trade;
import trade.api.User;
import trade.api.UserTrade;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnricherStatisticsByUserCollector {
	private final TopicResolver topicResolver;

	@Autowired
	public void buildPipeline(StreamsBuilder streamsBuilder) {
		Topic<Long, Trade> tradesTopic = topicResolver.findTopic(Topics.TRADES);
		Topic<Long, User> usersTopic = topicResolver.findTopic(Topics.USERS);
		Topic<Integer, UserTrade> userTradeTopic = topicResolver.findTopic(Topics.USER_TRADES);

		KStream<Integer, Trade> userTradesStream = streamsBuilder.stream(
						tradesTopic.topicName(),
						Consumed.with(tradesTopic.keySerde(), tradesTopic.valueSerde()).withName("trades"))
				.selectKey((key, value) -> value.getUserId(), Named.as("repartition-by-user-id"));

		KStream<Integer, User> usersStream =
				streamsBuilder
						.stream(
								usersTopic.topicName(),
								Consumed.with(usersTopic.keySerde(), usersTopic.valueSerde()).withName("read-users"))
						.selectKey((key, value) -> value.getUserId(), Named.as("repartition-users"));

		KTable<Integer, User> usersTable = usersStream.toTable(
				Materialized.<Integer, User, KeyValueStore<Bytes, byte[]>> as("usersStore")
						.withKeySerde(Serdes.Integer())
						.withValueSerde(usersTopic.valueSerde()));

		ValueJoiner<Trade, User, UserTrade> userTradeJoiner = (trade, user) -> UserTrade.newBuilder()
				.setUserId(trade.getUserId())
				.setQuantity(trade.getQuantity())
				.setSymbol(trade.getSymbol())
				.setAddressCountry(user.getAddressCountry())
				.build();

		KStream<Integer, UserTrade> enrichedTradeStream = userTradesStream.join(usersTable, userTradeJoiner,
				Joined.with(Serdes.Integer(), tradesTopic.valueSerde(), usersTopic.valueSerde()));

		enrichedTradeStream.foreach((key, value) -> log.info("Enriched record {} {}", key, value));

		enrichedTradeStream.to(
				userTradeTopic.topicName(),
				Produced.with(userTradeTopic.keySerde(), userTradeTopic.valueSerde()).withName("write-to-user-trades-topic"));
	}

}
