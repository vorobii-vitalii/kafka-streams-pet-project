package com.example.tradestatisticsaggregator.topics;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TopicResolver {
	private final Map<String, Topic<?, ?>> topicsMap;

	public TopicResolver(Collection<Topic<?, ?>> topics) {
		this.topicsMap = topics.stream()
				.collect(Collectors.toMap(Topic::topicName, v -> v));
	}

	@SuppressWarnings("unchecked")
	public <K, V> Topic<K, V> findTopic(String topicName) {
		return (Topic<K, V>) topicsMap.get(topicName);
	}

}
