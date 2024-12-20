package com.example.tradestatisticsaggregator.topology;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;

import com.example.tradestatisticsaggregator.KafkaTestUtils;
import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import trade.api.SymbolStats;
import trade.api.TopTradedSymbols;

class TestGlobalTopTradedProductsCollector {
	private static final String TOP_TRADED_STORE = "top-traded-store";
	private static final int IGNORED_KEY = 1;

	GlobalTopTradedProductsCollector globalTopTradedProductsCollector = new GlobalTopTradedProductsCollector(
			new TopicResolver(List.of(
					new Topic<>(Topics.SYMBOL_TRADES, Serdes.String(), Serdes.Long())
			)),
			KafkaTestUtils.getSerdeCreator()
	);

	@Test
	void buildPipeline() {
		Properties props = new Properties();
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "company-trades-count-collector");
		props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

		StreamsBuilder streamsBuilder = new StreamsBuilder();

		globalTopTradedProductsCollector.buildPipeline(streamsBuilder);

		try (TopologyTestDriver testDriver = new TopologyTestDriver(streamsBuilder.build(), props)) {
			TestInputTopic<String, Long> inputTopic =
					testDriver.createInputTopic(Topics.SYMBOL_TRADES, Serdes.String().serializer(), Serdes.Long().serializer());

			KeyValueStore<Integer, TopTradedSymbols> topTradedSymbolsStore = testDriver.getKeyValueStore(TOP_TRADED_STORE);

			inputTopic.pipeInput("ABBN", 100L);
			inputTopic.pipeInput("APPL", 30L);
			inputTopic.pipeInput("NVDA", 30L);

			assertContainsInAnyOrder(topTradedSymbolsStore, List.of(
					SymbolStats.newBuilder()
							.setSymbol("ABBN")
							.setTrades(100)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("APPL")
							.setTrades(30)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("NVDA")
							.setTrades(30)
							.build()
			));

			inputTopic.pipeInput("APPL", 50L);

			assertContainsInAnyOrder(topTradedSymbolsStore, List.of(
					SymbolStats.newBuilder()
							.setSymbol("ABBN")
							.setTrades(100)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("APPL")
							.setTrades(50)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("NVDA")
							.setTrades(30)
							.build()
			));

			inputTopic.pipeInput("TWTT", 900L);

			assertContainsInAnyOrder(topTradedSymbolsStore, List.of(
					SymbolStats.newBuilder()
							.setSymbol("ABBN")
							.setTrades(100)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("APPL")
							.setTrades(50)
							.build(),
					SymbolStats.newBuilder()
							.setSymbol("TWTT")
							.setTrades(900)
							.build()
			));

		}

	}

	private void assertContainsInAnyOrder(KeyValueStore<Integer, TopTradedSymbols> store, List<SymbolStats> symbolStats) {
		assertThat(store.get(IGNORED_KEY).getSymbols()).containsExactlyInAnyOrderElementsOf(symbolStats);
	}
}