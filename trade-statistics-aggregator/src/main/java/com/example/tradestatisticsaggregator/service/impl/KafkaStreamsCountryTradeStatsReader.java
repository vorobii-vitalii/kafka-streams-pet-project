package com.example.tradestatisticsaggregator.service.impl;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.example.tradestatisticsaggregator.dto.CountryStats;
import com.example.tradestatisticsaggregator.service.CountryTradeStatsReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaStreamsCountryTradeStatsReader implements CountryTradeStatsReader {
	private static final String TRADES_PER_COUNTRY = "trades-per-country";

	private final KafkaStreamsInteractiveQueryService interactiveQueryService;
	private final RestClient restClient;

	@Override
	public List<CountryStats> getCountryStatistics(String country) {
		HostInfo kafkaStreamsApplicationHostInfo =
				this.interactiveQueryService.getKafkaStreamsApplicationHostInfo(TRADES_PER_COUNTRY, country, new StringSerializer());
		if (Objects.equals(kafkaStreamsApplicationHostInfo, interactiveQueryService.getCurrentKafkaStreamsApplicationHostInfo())) {
			ReadOnlyWindowStore<String, ValueAndTimestamp<Long>> store =
					interactiveQueryService.retrieveQueryableStore(TRADES_PER_COUNTRY, QueryableStoreTypes.timestampedWindowStore());

			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
							store.fetch(country, Instant.now().minusSeconds(1000), Instant.now()), Spliterator.ORDERED), false)
					.map(v -> {
						return new CountryStats(Instant.ofEpochMilli(v.value.timestamp()), v.value.value());
					})
					.toList();
		}
		return Optional.of(restClient.get().uri(builder -> buildURI(country, builder, kafkaStreamsApplicationHostInfo)))
				.map(RestClient.RequestHeadersSpec::retrieve)
				.map(v -> v.body(new ParameterizedTypeReference<List<CountryStats>>() {
				}))
				.orElse(List.of());

	}

	private URI buildURI(String country, UriBuilder builder, HostInfo hostInfo) {
		return builder.host(hostInfo.host()).port(hostInfo.port()).scheme("http").path("/trade-stats/count/{country}").build(country);
	}
}
