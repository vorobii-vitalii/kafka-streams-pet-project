package com.example.tradestatisticsaggregator.rest;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tradestatisticsaggregator.service.TradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("trade-stats")
@RequiredArgsConstructor
@Slf4j
public class TradeStatsController {
	private final TradeStatsReader tradeStatsReader;

	// https://docs.confluent.io/platform/current/streams/developer-guide/interactive-queries.html
	@GetMapping
	@RequestMapping("/{symbol}")
	public ResponseEntity<Long> getNumberOfTrades(@PathVariable("symbol") String symbol) {
		return tradeStatsReader.getNumberOfTrades(symbol)
				.map(count -> new ResponseEntity<>(count, HttpStatusCode.valueOf(200)))
				.orElseGet(() -> new ResponseEntity<>(HttpStatusCode.valueOf(404)));
	}

}
