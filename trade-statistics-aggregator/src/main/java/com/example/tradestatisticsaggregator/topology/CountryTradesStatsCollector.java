package com.example.tradestatisticsaggregator.topology;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tradestatisticsaggregator.topics.Topic;
import com.example.tradestatisticsaggregator.topics.TopicResolver;
import com.example.tradestatisticsaggregator.topics.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trade.api.UserTrade;

@RequiredArgsConstructor
@Slf4j
@Component
public class CountryTradesStatsCollector {
	public static final Duration WINDOW_DURATION = Duration.ofSeconds(10);
	public static final Duration WINDOW_GRACE = Duration.ofSeconds(5);
	private final TopicResolver topicResolver;

	@Autowired
	public void buildPipeline(StreamsBuilder builder) {
		Topic<Integer, UserTrade> userTradeTopic = topicResolver.findTopic(Topics.USER_TRADES);

		KStream<Integer, UserTrade> userTradesStream = builder.stream(
				userTradeTopic.topicName(),
				Consumed.with(userTradeTopic.keySerde(), userTradeTopic.valueSerde()).withName("read-user-trades")
		);
		userTradesStream.foreach((key, value) -> log.info("User trade {} {}", key, value));

		KTable<Windowed<String>, Long> count =
				userTradesStream.groupBy((key, value) -> value.getAddressCountry(),
								Grouped.with(Serdes.String(), userTradeTopic.valueSerde()).withName("group-by-country"))
						.windowedBy(TimeWindows.ofSizeAndGrace(WINDOW_DURATION, WINDOW_GRACE))
						.count(Materialized.<String, Long, WindowStore<Bytes, byte[]>> as("trades-per-country")
								.withKeySerde(Serdes.String())
								.withValueSerde(Serdes.Long()));

		count.toStream().foreach((key, value) -> log.info("New trades per country stats {} {}", key, value));
	}

}
