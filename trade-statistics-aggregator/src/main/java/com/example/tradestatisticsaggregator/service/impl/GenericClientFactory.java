package com.example.tradestatisticsaggregator.service.impl;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.tradestatisticsaggregator.service.ClientFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericClientFactory<C> implements ClientFactory<C> {
	private final Class<C> clientType;

	@Override
	public C create(HostInfo hostInfo) {
		var exchangeAdapter = RestClientAdapter.create(RestClient.create("http://%s:%s/".formatted(hostInfo.host(), hostInfo.port())));
		HttpServiceProxyFactory httpServiceProxyFactory =
				HttpServiceProxyFactory.builderFor(exchangeAdapter)
						.build();
		return httpServiceProxyFactory.createClient(clientType);
	}
}
