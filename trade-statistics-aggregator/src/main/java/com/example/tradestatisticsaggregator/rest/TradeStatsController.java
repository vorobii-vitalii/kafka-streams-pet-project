package com.example.tradestatisticsaggregator.rest;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.tradestatisticsaggregator.dto.CountryStats;
import com.example.tradestatisticsaggregator.dto.GlobalTopTradedStats;
import com.example.tradestatisticsaggregator.service.CountryTradeStatsReader;
import com.example.tradestatisticsaggregator.service.GlobalTopTradedStatsReader;
import com.example.tradestatisticsaggregator.service.SymbolTradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TradeStatsController implements TradeStatsService {
	private final SymbolTradeStatsReader symbolTradeStatsReader;
	private final CountryTradeStatsReader countryTradeStatsReader;
	private final GlobalTopTradedStatsReader globalTopTradedStatsReader;

	@Override
	public ResponseEntity<Long> getNumberOfTrades(@PathVariable("symbol") String symbol) {
		return symbolTradeStatsReader.getNumberOfTrades(symbol)
				.map(count -> new ResponseEntity<>(count, HttpStatusCode.valueOf(200)))
				.orElseGet(() -> new ResponseEntity<>(HttpStatusCode.valueOf(404)));
	}

	@Override
	public ResponseEntity<List<CountryStats>> getCountryStats(@PathVariable("country") String county) {
		return new ResponseEntity<>(countryTradeStatsReader.getCountryStatistics(county), HttpStatusCode.valueOf(200));
	}

	@Override
	public ResponseEntity<GlobalTopTradedStats> getGlobalTopTradedStats() {
		return new ResponseEntity<>(globalTopTradedStatsReader.getGlobalTopTradedStats(), HttpStatusCode.valueOf(200));
	}

}
