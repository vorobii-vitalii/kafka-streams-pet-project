package com.example.tradestatisticsaggregator.service;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.streams.state.HostInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestClientCreator<C> {
	private final ClientFactory<C> clientFactory;
	private final ConcurrentHashMap<HostInfo, C> cachedClients = new ConcurrentHashMap<>();

	public C createClient(HostInfo hostInfo) {
		return cachedClients.computeIfAbsent(hostInfo, clientFactory::create);
	}

}
