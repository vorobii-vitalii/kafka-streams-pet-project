package com.example.tradestatisticsaggregator.avro;

import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

@Component
public class SerdeCreator {
	private final String schemaRegistryUrl;

	public SerdeCreator(@Value("${schema.registry.url}") String schemaRegistryUrl) {
		this.schemaRegistryUrl = schemaRegistryUrl;
	}

	public  <T extends SpecificRecord> Serde<T> createSerde(boolean isKeyType) {
		Serde<T> serde = new SpecificAvroSerde<>();
		serde.configure(Map.of("schema.registry.url", schemaRegistryUrl), isKeyType);
		return serde;
	}


}
