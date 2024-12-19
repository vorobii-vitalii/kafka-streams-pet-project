package com.example.tradestatisticsaggregator;

import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class KafkaTestUtils {

	public static <T extends SpecificRecord> Serde<T> createAvroSerde(boolean isKeyType) {
		Serde<T> serde = new SpecificAvroSerde<>();
		serde.configure(Map.of("schema.registry.url", "mock://testurl"), isKeyType);
		return serde;
	}

}
