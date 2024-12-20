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

		List<StatsBatch> trades = List.of(
				new StatsBatch("ABBN", 90),
				new StatsBatch("APPL", 90),
				new StatsBatch("NVDA", 900),
				new StatsBatch("SQWN", 3)
		);

		for (int i = 0; i < trades.size(); i++) {
			StatsBatch statsBatch = trades.get(i);
			for (int j = 0; j < statsBatch.count(); j++) {
				int userId = (i + j) % 4;
				if (userId == 0) {
					userId = 4;
				}
				RecordMetadata metadata = tradesProducer.send(new ProducerRecord<>(
						"trades",
						(long) i,
						Trade.newBuilder()
								.setSymbol(statsBatch.symbol())
								.setQuantity(1)
								.setUserId(userId)
								.build()
				)).get();
				System.out.println("Metadata = " + metadata);
			}
		}
		System.out.println("Mock trades sent!");
	}

	record StatsBatch(String symbol, int count) {

	}
}
