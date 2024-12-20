package com.example.tradestatisticsaggregator.service.impl;

import java.util.Objects;

import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.stereotype.Service;

import com.example.tradestatisticsaggregator.dto.CompanyTradeStats;
import com.example.tradestatisticsaggregator.dto.GlobalTopTradedStats;
import com.example.tradestatisticsaggregator.rest.TradeStatsService;
import com.example.tradestatisticsaggregator.service.GlobalTopTradedStatsReader;
import com.example.tradestatisticsaggregator.service.RestClientCreator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trade.api.TopTradedSymbols;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaStreamsGlobalTopTradedStatsReader implements GlobalTopTradedStatsReader {
	private static final String TOP_TRADED_STORE = "top-traded-store";
	private static final int IGNORED_KEY = 1;

	private final KafkaStreamsInteractiveQueryService interactiveQueryService;
	private final RestClientCreator<TradeStatsService> tradeStatsServiceCreator;

	@Override
	public GlobalTopTradedStats getGlobalTopTradedStats() {
		HostInfo kafkaStreamsApplicationHostInfo =
				this.interactiveQueryService.getKafkaStreamsApplicationHostInfo(TOP_TRADED_STORE, IGNORED_KEY, new IntegerSerializer());
		if (Objects.equals(kafkaStreamsApplicationHostInfo, interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())) {
			ReadOnlyKeyValueStore<Integer, TopTradedSymbols> store =
					interactiveQueryService.retrieveQueryableStore(TOP_TRADED_STORE, QueryableStoreTypes.keyValueStore());
			TopTradedSymbols topTradedSymbols = store.get(IGNORED_KEY);
			return GlobalTopTradedStats.builder()
					.topTradedCompanies(topTradedSymbols.getSymbols().stream()
							.map(v -> CompanyTradeStats.builder().symbol(v.getSymbol()).tradeCount(v.getTrades()).build())
							.toList())
					.build();
		}
		return tradeStatsServiceCreator.createClient(kafkaStreamsApplicationHostInfo).getGlobalTopTradedStats().getBody();
	}

}
