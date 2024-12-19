package com.example.tradestatisticsaggregator.topology;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.junit.jupiter.api.Test;

import com.example.tradestatisticsaggregator.KafkaTestUtils;
import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import trade.api.UserTrade;

class TestCountryTradesStatsCollector {

	private final Serde<UserTrade> userTradeSerde = KafkaTestUtils.createAvroSerde(false);

	CountryTradesStatsCollector countryTradesStatsCollector = new CountryTradesStatsCollector(
			new TopicResolver(List.of(
					new Topic<>(Topics.USER_TRADES, Serdes.Integer(), userTradeSerde)
			)));

	@Test
	void buildPipeline() {
		Properties props = new Properties();
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "company-trades-count-collector");
		props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

		StreamsBuilder streamsBuilder = new StreamsBuilder();

		countryTradesStatsCollector.buildPipeline(streamsBuilder);

		try (TopologyTestDriver testDriver = new TopologyTestDriver(streamsBuilder.build(), props)) {
			TestInputTopic<Integer, UserTrade> inputTopic =
					testDriver.createInputTopic(Topics.USER_TRADES, Serdes.Integer().serializer(), userTradeSerde.serializer());

			for (int i = 0; i < 100; i++) {
				inputTopic.pipeInput(UserTrade.newBuilder()
						.setUserId(i)
						.setQuantity(1)
						.setSymbol("ABBN")
						.setAddressCountry("USA")
						.build());
			}
			for (int i = 0; i < 50; i++) {
				inputTopic.pipeInput(UserTrade.newBuilder()
						.setUserId(i + 100)
						.setQuantity(1)
						.setSymbol("APPL")
						.setAddressCountry("Canada")
						.build());
			}
			testDriver.advanceWallClockTime(Duration.ofSeconds(20));

			assertThat(getStats(testDriver, "USA")).containsExactly(100L);
			assertThat(getStats(testDriver, "Canada")).containsExactly(50L);
		}

	}

	private List<Long> getStats(TopologyTestDriver testDriver, String country) {
		WindowStore<String, Long> store = testDriver.getWindowStore("trades-per-country");
		WindowStoreIterator<Long> iterator = store.fetch(country, Instant.now().minusSeconds(1000), Instant.now());
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
				.map(v -> v.value)
				.toList();
	}

}
