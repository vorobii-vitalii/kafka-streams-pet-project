package com.example.tradestatisticsaggregator.topology;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
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

import trade.api.Trade;

class TestCompanyTradesCountCollector {

	private final Serde<Trade> tradeSerde = KafkaTestUtils.createAvroSerde(false);

	CompanyTradesCountCollector companyTradesCountCollector = new CompanyTradesCountCollector(
			new TopicResolver(List.of(
					new Topic<>(Topics.TRADES, Serdes.Long(), tradeSerde),
					new Topic<>(Topics.SYMBOL_TRADES, Serdes.String(), Serdes.Long())
			)));

	@Test
	void buildPipeline() {
		Properties props = new Properties();
		props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "company-trades-count-collector");
		props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

		StreamsBuilder streamsBuilder = new StreamsBuilder();

		companyTradesCountCollector.buildPipeline(streamsBuilder);

		try (TopologyTestDriver testDriver = new TopologyTestDriver(streamsBuilder.build(), props)) {
			TestInputTopic<Long, Trade> inputTopic =
					testDriver.createInputTopic(Topics.TRADES, Serdes.Long().serializer(), tradeSerde.serializer());

			KeyValueStore<String, Long> keyValueStore = testDriver.getKeyValueStore("symbol-trades-store");

			for (int i = 0; i < 100; i++) {
				inputTopic.pipeInput(new Trade("ABBN", i + 1, 1));
			}
			assertThat(keyValueStore.get("ABBN")).isEqualTo(100);
			for (int i = 0; i < 50; i++) {
				inputTopic.pipeInput(new Trade("ABBN", i + 1, 1));
			}
			assertThat(keyValueStore.get("ABBN")).isEqualTo(150);
		}

	}

}
