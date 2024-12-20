package com.example.tradestatisticsaggregator.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;

import com.example.tradestatisticsaggregator.rest.TradeStatsService;
import com.example.tradestatisticsaggregator.service.RestClientCreator;

@ExtendWith(MockitoExtension.class)
class TestKafkaStreamsSymbolTradeStatsReader {
	public static final String SYMBOL_TRADES_STORE = "symbol-trades-store";
	public static final String SYMBOL = "ABBN";

	@Mock
	KafkaStreamsInteractiveQueryService interactiveQueryService;

	@Mock
	RestClientCreator<TradeStatsService> tradeStatsServiceCreator;

	@Mock
	ReadOnlyKeyValueStore<String, Long> store;

	@Mock
	TradeStatsService tradeStatsService;

	@InjectMocks
	KafkaStreamsSymbolTradeStatsReader symbolTradeStatsReader;

	@Test
	void getNumberOfTradesGivenDataStoredLocally() {
		// Given
		when(interactiveQueryService.getKafkaStreamsApplicationHostInfo(eq(SYMBOL_TRADES_STORE), eq(SYMBOL), any()))
				.thenReturn(new HostInfo("localhost", 9092));
		when(interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())
				.thenReturn(new HostInfo("localhost", 9092));

		when(interactiveQueryService.retrieveQueryableStore(eq(SYMBOL_TRADES_STORE), any()))
				.thenReturn(store);
		when(store.get(SYMBOL)).thenReturn(39L);

		// When
		Optional<Long> numberOfTrades = symbolTradeStatsReader.getNumberOfTrades(SYMBOL);

		// Then
		assertThat(numberOfTrades).contains(39L);
	}

	@Test
	void getNumberOfTradesGivenDataStoredOnAnotherInstance() {
		// Given
		when(interactiveQueryService.getKafkaStreamsApplicationHostInfo(eq(SYMBOL_TRADES_STORE), eq(SYMBOL), any()))
				.thenReturn(new HostInfo("localhost", 9092));
		when(interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())
				.thenReturn(new HostInfo("localhost", 9093));

		when(tradeStatsServiceCreator.createClient(new HostInfo("localhost", 9092)))
				.thenReturn(tradeStatsService);

		when(tradeStatsService.getNumberOfTrades(SYMBOL)).thenReturn(ResponseEntity.of(Optional.of(100L)));

		// When
		Optional<Long> numberOfTrades = symbolTradeStatsReader.getNumberOfTrades(SYMBOL);

		// Then
		assertThat(numberOfTrades).contains(100L);
	}

}