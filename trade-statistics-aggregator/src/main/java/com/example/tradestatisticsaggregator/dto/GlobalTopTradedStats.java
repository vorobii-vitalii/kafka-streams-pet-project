package com.example.tradestatisticsaggregator.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record GlobalTopTradedStats(List<CompanyTradeStats> topTradedCompanies) {

}
