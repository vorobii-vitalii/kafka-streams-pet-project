package com.example.tradestatisticsaggregator.rest;

import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("trade-stats")
@RequiredArgsConstructor
@Slf4j
public class TradeStatsController {
	private final KafkaStreamsInteractiveQueryService interactiveQueryService;

	// https://docs.confluent.io/platform/current/streams/developer-guide/interactive-queries.html
	@GetMapping
	@RequestMapping("/{symbol}")
	public ResponseEntity<Long> getNumberOfTrades(@PathVariable("symbol") String symbol) {
		ReadOnlyKeyValueStore<String, Long> store =
				interactiveQueryService.retrieveQueryableStore("symbol-trades-store", QueryableStoreTypes.keyValueStore());
		Long count = store.get(symbol);
		if (count == null) {
			return new ResponseEntity<>(HttpStatusCode.valueOf(404));
		}
		return new ResponseEntity<>(count, HttpStatusCode.valueOf(200));
	}

}
