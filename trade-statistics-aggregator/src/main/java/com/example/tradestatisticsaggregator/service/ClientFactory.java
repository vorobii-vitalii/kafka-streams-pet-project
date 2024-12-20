package com.example.tradestatisticsaggregator.service;

import org.apache.kafka.streams.state.HostInfo;

public interface ClientFactory<C> {
	C create(HostInfo hostInfo);
}
