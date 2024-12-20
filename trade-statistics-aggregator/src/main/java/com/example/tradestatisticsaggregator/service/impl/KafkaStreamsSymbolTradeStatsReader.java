package com.example.tradestatisticsaggregator.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpEntity;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.stereotype.Service;

import com.example.tradestatisticsaggregator.rest.TradeStatsService;
import com.example.tradestatisticsaggregator.service.RestClientCreator;
import com.example.tradestatisticsaggregator.service.SymbolTradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaStreamsSymbolTradeStatsReader implements SymbolTradeStatsReader {
	public static final String SYMBOL_TRADES_STORE = "symbol-trades-store";

	private final KafkaStreamsInteractiveQueryService interactiveQueryService;
	private final RestClientCreator<TradeStatsService> tradeStatsServiceCreator;

	@Override
	public Optional<Long> getNumberOfTrades(String symbol) {
		HostInfo kafkaStreamsApplicationHostInfo =
				this.interactiveQueryService.getKafkaStreamsApplicationHostInfo(SYMBOL_TRADES_STORE, symbol, new StringSerializer());
		if (Objects.equals(kafkaStreamsApplicationHostInfo, interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())) {
			ReadOnlyKeyValueStore<String, Long> store =
					interactiveQueryService.retrieveQueryableStore(SYMBOL_TRADES_STORE, QueryableStoreTypes.keyValueStore());
			return Optional.ofNullable(store.get(symbol));
		}
		return Optional.ofNullable(tradeStatsServiceCreator.createClient(kafkaStreamsApplicationHostInfo).getNumberOfTrades(symbol))
				.filter(v -> v.getStatusCode().is2xxSuccessful())
				.map(HttpEntity::getBody);
	}

}
