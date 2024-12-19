package com.example.tradestatisticsaggregator.service;

import java.util.List;

import com.example.tradestatisticsaggregator.dto.CountryStats;

public interface CountryTradeStatsReader {
	List<CountryStats> getCountryStatistics(String country);
}
