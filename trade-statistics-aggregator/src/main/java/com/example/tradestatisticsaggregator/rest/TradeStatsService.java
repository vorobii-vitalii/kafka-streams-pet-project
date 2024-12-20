package com.example.tradestatisticsaggregator.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.tradestatisticsaggregator.dto.CountryStats;
import com.example.tradestatisticsaggregator.dto.GlobalTopTradedStats;

@HttpExchange(url = "trade-stats", accept = MediaType.APPLICATION_JSON_VALUE)
public interface TradeStatsService {

	@GetExchange("/{symbol}")
	ResponseEntity<Long> getNumberOfTrades(@PathVariable String symbol);

	@GetExchange("/country/{country}")
	ResponseEntity<List<CountryStats>> getCountryStats(@PathVariable("country") String county);

	@GetExchange("/top-traded")
	ResponseEntity<GlobalTopTradedStats> getGlobalTopTradedStats();

}
