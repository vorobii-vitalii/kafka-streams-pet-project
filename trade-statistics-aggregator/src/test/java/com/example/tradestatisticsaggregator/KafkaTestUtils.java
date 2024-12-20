package com.example.tradestatisticsaggregator;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;

import com.example.tradestatisticsaggregator.avro.SerdeCreator;

public class KafkaTestUtils {
	private static final SerdeCreator serdeCreator = new SerdeCreator("mock://testurl");

	public static <T extends SpecificRecord> Serde<T> createAvroSerde(boolean isKeyType) {
		return serdeCreator.createSerde(isKeyType);
	}

	public static SerdeCreator getSerdeCreator() {
		return serdeCreator;
	}
}
