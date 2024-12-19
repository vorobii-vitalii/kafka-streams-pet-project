package com.example.tradestatisticsaggregator.service;

import java.util.Optional;

import javax.swing.text.html.Option;

public interface TradeStatsReader {
	Optional<Long> getNumberOfTrades(String symbol);
}
