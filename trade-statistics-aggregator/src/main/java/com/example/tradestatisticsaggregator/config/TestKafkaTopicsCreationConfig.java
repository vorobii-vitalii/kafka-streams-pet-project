package com.example.tradestatisticsaggregator.config;

import java.util.List;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import com.example.tradestatisticsaggregator.topics.Topic;

@Configuration
@ConditionalOnProperty("spring.kafka.admin.auto-create")
public class TestKafkaTopicsCreationConfig {

	public static final int PARTITION_COUNT = 2;
	public static final int REPLICA_COUNT = 1;

	@Bean
	KafkaAdmin.NewTopics newTopics(List<Topic<?, ?>> topics) {
		var newTopicsArray = topics.stream()
				.map(v -> TopicBuilder.name(v.topicName()).partitions(PARTITION_COUNT).replicas(REPLICA_COUNT).build())
				.toArray(NewTopic[]::new);
		return new KafkaAdmin.NewTopics(newTopicsArray);
	}

}
