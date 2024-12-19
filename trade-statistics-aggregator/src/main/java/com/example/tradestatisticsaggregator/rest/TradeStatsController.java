package com.example.tradestatisticsaggregator.rest;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tradestatisticsaggregator.dto.CountryStats;
import com.example.tradestatisticsaggregator.service.CountryTradeStatsReader;
import com.example.tradestatisticsaggregator.service.SymbolTradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("trade-stats")
@RequiredArgsConstructor
@Slf4j
public class TradeStatsController {
	private final SymbolTradeStatsReader symbolTradeStatsReader;
	private final CountryTradeStatsReader countryTradeStatsReader;

	@GetMapping
	@RequestMapping("/{symbol}")
	public ResponseEntity<Long> getNumberOfTrades(@PathVariable("symbol") String symbol) {
		return symbolTradeStatsReader.getNumberOfTrades(symbol)
				.map(count -> new ResponseEntity<>(count, HttpStatusCode.valueOf(200)))
				.orElseGet(() -> new ResponseEntity<>(HttpStatusCode.valueOf(404)));
	}

	@GetMapping
	@RequestMapping("/country/{country}")
	public ResponseEntity<List<CountryStats>> getCountryStats(@PathVariable("country") String county) {
		return new ResponseEntity<>(countryTradeStatsReader.getCountryStatistics(county), HttpStatusCode.valueOf(200));
	}

}
