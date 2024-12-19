package com.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import trade.api.User;

public class MockUsersProducer {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpecificAvroSerializer<User> tradeSerializer = new SpecificAvroSerializer<>();
		tradeSerializer.configure(Map.of("schema.registry.url", "http://127.0.0.1:8081"), false);
		Producer<Long, User> tradesProducer = ProducerProvider.createProducer(new LongSerializer(), tradeSerializer);

		List<User> users = List.of(
				User.newBuilder()
						.setUserId(1)
						.setAddressCountry("Ukraine")
						.build(),
				User.newBuilder()
						.setUserId(2)
						.setAddressCountry("USA")
						.build(),
				User.newBuilder()
						.setUserId(3)
						.setAddressCountry("Ukraine")
						.build(),
				User.newBuilder()
						.setUserId(4)
						.setAddressCountry("Poland")
						.build()
		);
		for (int i = 0; i < users.size(); i++) {
			RecordMetadata metadata = tradesProducer.send(new ProducerRecord<>(
					"users",
					(long) i,
					users.get(i)
			)).get();
			System.out.println("Metadata = " + metadata);
		}
		System.out.println("Mock users sent!");

	}

}
