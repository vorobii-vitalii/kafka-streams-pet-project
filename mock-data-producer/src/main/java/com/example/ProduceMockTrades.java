package com.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import trade.api.Trade;

public class ProduceMockTrades {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpecificAvroSerializer<Trade> tradeSerializer = new SpecificAvroSerializer<>();
		tradeSerializer.configure(Map.of("schema.registry.url", "http://127.0.0.1:8081"), false);
		Producer<Long, Trade> tradesProducer = ProducerProvider.createProducer(new LongSerializer(), tradeSerializer);

		List<Trade> trades = List.of(
				Trade.newBuilder()
						.setSymbol("ABBN")
						.setQuantity(1)
						.build(),
				Trade.newBuilder()
						.setSymbol("APPL")
						.setQuantity(2)
						.build(),
				Trade.newBuilder()
						.setSymbol("ABBN")
						.setQuantity(3)
						.build(),
				Trade.newBuilder()
						.setSymbol("ABBN")
						.setQuantity(1)
						.build()
		);
		for (int i = 0; i < trades.size(); i++) {
			RecordMetadata metadata = tradesProducer.send(new ProducerRecord<>(
					"trades",
					(long) i,
					trades.get(i)
			)).get();
			System.out.println("Metadata = " + metadata);
		}
		System.out.println("Mock trades sent!");
	}
}
