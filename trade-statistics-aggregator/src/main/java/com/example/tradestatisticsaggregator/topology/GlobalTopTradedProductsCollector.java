package com.example.tradestatisticsaggregator.topology;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tradestatisticsaggregator.avro.SerdeCreator;
import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trade.api.SymbolStats;
import trade.api.TopTradedSymbols;

@RequiredArgsConstructor
@Component
@Slf4j
public class GlobalTopTradedProductsCollector {
	private static final int IGNORED_KEY = 1;
	private static final int N = 3;
	public static final String TOP_TRADED_STORE = "top-traded-store";

	private final TopicResolver topicResolver;
	private final SerdeCreator serdeCreator;

	@Autowired
	public void buildPipeline(StreamsBuilder builder) {
		Topic<String, Long> symbolTradesTopic = topicResolver.findTopic(Topics.SYMBOL_TRADES);

		KStream<Integer, SymbolStats> symbolTrades = builder.stream(symbolTradesTopic.topicName(),
						Consumed.with(symbolTradesTopic.keySerde(), symbolTradesTopic.valueSerde()).withName("symbol-trades-topic"))
				.repartition(Repartitioned.with(symbolTradesTopic.keySerde(), symbolTradesTopic.valueSerde())
						.withName("symbol-trades-repartitioned-topic-single-partition")
						.withNumberOfPartitions(1))
				.map((key, value) -> KeyValue.pair(IGNORED_KEY, new SymbolStats(key, value.intValue())));

		Materialized<Integer, TopTradedSymbols, KeyValueStore<Bytes, byte[]>> objectObjectStateStoreMaterialized =
				Materialized.<Integer, TopTradedSymbols, KeyValueStore<Bytes, byte[]>> as(TOP_TRADED_STORE)
						.withKeySerde(Serdes.Integer())
						.withValueSerde(serdeCreator.createSerde(false));

		KTable<Integer, TopTradedSymbols> topTradedSymbolsTable =
				symbolTrades.groupByKey(Grouped.with(Serdes.Integer(), serdeCreator.<SymbolStats> createSerde(false))
								.withName("grouped-symbol-trades-topic"))
						.aggregate(
								() -> TopTradedSymbols.newBuilder().setSymbols(List.of()).build(),
								this::applyNewStats,
								objectObjectStateStoreMaterialized
						);
	}

	private TopTradedSymbols applyNewStats(int key, SymbolStats newStats, TopTradedSymbols aggregate) {
		PriorityQueue<SymbolStats> symbolStats = new PriorityQueue<>(Comparator.comparingInt(SymbolStats::getTrades));
		boolean hasUpdated = false;
		for (SymbolStats symbol : aggregate.getSymbols()) {
			if (Objects.equals(symbol.getSymbol(), newStats.getSymbol())) {
				symbolStats.offer(newStats);
				hasUpdated = true;
			} else {
				symbolStats.offer(symbol);
			}
		}
		if (!hasUpdated) {
			symbolStats.add(newStats);
		}
		if (symbolStats.size() > N) {
			symbolStats.poll();
		}
		return TopTradedSymbols.newBuilder()
				.setSymbols(new ArrayList<>(symbolStats))
				.build();
	}
}
