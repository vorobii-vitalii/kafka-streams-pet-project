package com.example.tradestatisticsaggregator.service.impl;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.tradestatisticsaggregator.rest.TradeStatsService;
import com.example.tradestatisticsaggregator.service.ClientFactory;

@Component
public class TradeStatsServiceFactory implements ClientFactory<TradeStatsService> {

	@Override
	public TradeStatsService create(HostInfo hostInfo) {
		var exchangeAdapter = RestClientAdapter.create(RestClient.create("http://%s:%s/".formatted(hostInfo.host(), hostInfo.port())));
		HttpServiceProxyFactory httpServiceProxyFactory =
				HttpServiceProxyFactory.builderFor(exchangeAdapter)
						.build();
		return httpServiceProxyFactory.createClient(TradeStatsService.class);
	}
}
