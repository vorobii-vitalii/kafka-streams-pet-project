package com.example.tradestatisticsaggregator.topics;

import org.apache.kafka.common.serialization.Serde;

public record Topic<K, V>(String topicName, Serde<K> keySerde, Serde<V> valueSerde) {

}
