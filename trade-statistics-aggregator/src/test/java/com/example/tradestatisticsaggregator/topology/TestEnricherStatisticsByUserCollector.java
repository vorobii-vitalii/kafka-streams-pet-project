package com.example.tradestatisticsaggregator.topology;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;

import com.example.tradestatisticsaggregator.KafkaTestUtils;
import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import trade.api.Trade;
import trade.api.User;
import trade.api.UserTrade;

class TestEnricherStatisticsByUserCollector {

	private final Serde<Trade> tradeSerde = KafkaTestUtils.createAvroSerde(false);
	private final Serde<User> userSerde = KafkaTestUtils.createAvroSerde(false);
	private final Serde<UserTrade> userTradeSerde = KafkaTestUtils.createAvroSerde(false);

	EnricherStatisticsByUserCollector enricherStatisticsByUserCollector = new EnricherStatisticsByUserCollector(
			new TopicResolver(List.of(
					new Topic<>(Topics.TRADES, Serdes.Long(), tradeSerde),
					new Topic<>(Topics.USERS, Serdes.Long(), userSerde),
					new Topic<>(Topics.USER_TRADES, Serdes.Integer(), userTradeSerde)
			)));

	@Test
	void buildPipeline() {
		Properties props = new Properties();
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "company-trades-count-collector");
		props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

		StreamsBuilder streamsBuilder = new StreamsBuilder();

		enricherStatisticsByUserCollector.buildPipeline(streamsBuilder);

		try (TopologyTestDriver testDriver = new TopologyTestDriver(streamsBuilder.build(), props)) {
			TestInputTopic<Long, Trade> tradesTopic =
					testDriver.createInputTopic(Topics.TRADES, Serdes.Long().serializer(), tradeSerde.serializer());
			TestInputTopic<Long, User> usersTopic =
					testDriver.createInputTopic(Topics.USERS, Serdes.Long().serializer(), userSerde.serializer());

			TestOutputTopic<Integer, UserTrade> userTradeOutputTopic =
					testDriver.createOutputTopic(Topics.USER_TRADES, Serdes.Integer().deserializer(), userTradeSerde.deserializer());

			usersTopic.pipeInput(
					User.newBuilder()
							.setUserId(1)
							.setAddressCountry("USA")
							.build());

			usersTopic.pipeInput(
					User.newBuilder()
							.setUserId(2)
							.setAddressCountry("Canada")
							.build());

			tradesTopic.pipeInput(
					Trade.newBuilder()
							.setSymbol("ABBN")
							.setQuantity(1)
							.setUserId(1)
							.build());
			tradesTopic.pipeInput(
					Trade.newBuilder()
							.setSymbol("APPL")
							.setQuantity(2)
							.setUserId(2)
							.build());

			assertThat(userTradeOutputTopic.readValue())
					.isEqualTo(
							UserTrade.newBuilder()
									.setUserId(1)
									.setQuantity(1)
									.setSymbol("ABBN")
									.setAddressCountry("USA")
									.build());
			assertThat(userTradeOutputTopic.readValue())
					.isEqualTo(
							UserTrade.newBuilder()
									.setUserId(2)
									.setQuantity(2)
									.setSymbol("APPL")
									.setAddressCountry("Canada")
									.build());
			assertThat(userTradeOutputTopic.isEmpty()).isTrue();
		}
	}
}