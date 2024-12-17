package com.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;

public class ProducerProvider {

	public static <K, V> Producer<K, V> createProducer(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.LINGER_MS_CONFIG, "0");
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, "mock.producer");
		properties.put("schema.registry.url", "http://127.0.0.1:8081");
		return new KafkaProducer<>(properties, keySerializer, valueSerializer);
	}

}
