package com.example.tradestatisticsaggregator.service.impl;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpEntity;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.tradestatisticsaggregator.service.TradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaStreamsTradeStatsReader implements TradeStatsReader {
	public static final String SYMBOL_TRADES_STORE = "symbol-trades-store";

	private final KafkaStreamsInteractiveQueryService interactiveQueryService;
	private final RestTemplate restTemplate;

	@Override
	public Optional<Long> getNumberOfTrades(String symbol) {
		HostInfo kafkaStreamsApplicationHostInfo =
				this.interactiveQueryService.getKafkaStreamsApplicationHostInfo(SYMBOL_TRADES_STORE, symbol, new StringSerializer());

		if (Objects.equals(kafkaStreamsApplicationHostInfo, interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())) {
			ReadOnlyKeyValueStore<String, Long> store =
					interactiveQueryService.retrieveQueryableStore(SYMBOL_TRADES_STORE, QueryableStoreTypes.keyValueStore());
			return Optional.ofNullable(store.get(symbol));
		}
		return Optional.of(restTemplate.getForEntity(getUrl(symbol, kafkaStreamsApplicationHostInfo), Long.class))
				.filter(v -> v.getStatusCode().is2xxSuccessful())
				.map(HttpEntity::getBody);
	}

	private static URI getUrl(String symbol, HostInfo kafkaStreamsApplicationHostInfo) {
		return URI.create("http:// " + kafkaStreamsApplicationHostInfo.host() + ":" + kafkaStreamsApplicationHostInfo.port() + "/trade-stats/" + symbol);
	}
}
