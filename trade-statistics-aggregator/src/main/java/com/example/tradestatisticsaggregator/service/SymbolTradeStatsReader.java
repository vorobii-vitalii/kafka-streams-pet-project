package com.example.tradestatisticsaggregator.service;

import java.util.Optional;

public interface SymbolTradeStatsReader {
	Optional<Long> getNumberOfTrades(String symbol);
}
