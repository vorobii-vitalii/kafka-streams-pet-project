package com.example.tradestatisticsaggregator.topology;

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
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trade.api.Trade;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyTradesCountCollector {
	private final TopicResolver topicResolver;

	@Autowired
	public void buildPipeline(StreamsBuilder streamsBuilder) {
		Topic<Long, Trade> tradesTopic = topicResolver.findTopic(Topics.TRADES);
		Topic<String, Long> symbolTrades = topicResolver.findTopic(Topics.SYMBOL_TRADES);

		KStream<Long, Trade> tradesStream = streamsBuilder.stream(
				tradesTopic.topicName(),
				Consumed.with(tradesTopic.keySerde(), tradesTopic.valueSerde()).withName("trades"));

		tradesStream.foreach((key, value) -> log.info("New trade {} {}", key, value));

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

		KStream<String, Long> statsStream = countBySymbolTable.toStream(Named.as("convert-table-to-stream"));

		statsStream.foreach((key, value) -> log.info("New trade stats update {} {}", key, value));

		statsStream.to(symbolTrades.topicName(), Produced.with(symbolTrades.keySerde(), symbolTrades.valueSerde()));
	}
}
