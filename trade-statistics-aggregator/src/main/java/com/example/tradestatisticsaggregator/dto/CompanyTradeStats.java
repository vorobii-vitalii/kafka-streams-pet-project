package com.example.tradestatisticsaggregator.dto;

import lombok.Builder;

@Builder
public record CompanyTradeStats(String symbol, int tradeCount) {
}
