package com.example.tradestatisticsaggregator.topology;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import lombok.RequiredArgsConstructor;
import trade.api.Trade;

@Component
@RequiredArgsConstructor
public class CompanyTradesCountCollector {
	private final TopicResolver topicResolver;

	@Autowired
	public void buildPipeline(StreamsBuilder streamsBuilder) {
		Topic<Long, Trade> tradesTopic = topicResolver.findTopic(Topics.TRADES);
		Topic<String, Long> symbolTrades = topicResolver.findTopic(Topics.SYMBOL_TRADES);

		KStream<Long, Trade> tradesStream = streamsBuilder.stream(
				tradesTopic.topicName(),
				Consumed.with(tradesTopic.keySerde(), tradesTopic.valueSerde()).withName("trades"));

		KGroupedStream<String, Trade> groupedBySymbol = tradesStream.groupBy(
				(key, value) -> value.getSymbol(),
				Grouped.<String, Trade> as("group-by-symbol")
						.withKeySerde(Serdes.String())
						.withValueSerde(tradesTopic.valueSerde()));

		Materialized<String, Long, KeyValueStore<Bytes, byte[]>> materializedCount =
				Materialized.<String, Long, KeyValueStore<Bytes, byte[]>> as("symbol-trades-store")
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Long());

		KTable<String, Long> countBySymbolTable = groupedBySymbol.count(materializedCount);

		countBySymbolTable
				.suppress(Suppressed.untilTimeLimit(Duration.ofHours(2), Suppressed.BufferConfig.unbounded()).withName("delay-stats-update"))
				.toStream(Named.as("convert-table-to-stream"))
				.to(symbolTrades.topicName(), Produced.with(symbolTrades.keySerde(), symbolTrades.valueSerde()));
	}
}
