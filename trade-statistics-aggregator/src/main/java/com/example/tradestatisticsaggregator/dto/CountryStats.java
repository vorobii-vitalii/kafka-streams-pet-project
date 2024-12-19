package com.example.tradestatisticsaggregator.dto;

import java.time.Instant;

public record CountryStats(Instant timestamp, long numTrades) {
}
