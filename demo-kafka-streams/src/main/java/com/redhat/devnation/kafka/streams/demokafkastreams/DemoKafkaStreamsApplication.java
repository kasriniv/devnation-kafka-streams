package com.redhat.devnation.kafka.streams.demokafkastreams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class DemoKafkaStreamsApplication {

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testStreams");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
		return new StreamsConfig(props);
	}

	@Bean
	public KStream<String, Long> kStream(StreamsBuilder builder) {

		final KStream<String, String> textLines = builder.stream("streams-plaintext-input");

		final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
                /*
		final KTable<String, Long> wordCounts = textLines
				// Split each text line, by whitespace, into words.  The text lines are the record
				// values, i.e. we can ignore whatever data is in the record keys and thus invoke
				// `flatMapValues()` instead of the more generic `flatMap()`.
				.flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
				// Count the occurrences of each word (record key).
				//
				// This will change the stream type from `KStream<String, String>` to `KTable<String, Long>`
				// (word -> count).  In the `count` operation we must provide a name for the resulting KTable,
				// which will be used to name e.g. its associated state store and changelog topic.
				//
				// Note: no need to specify explicit serdes because the resulting key and value types match our default serde settings
				.groupBy((key, word) -> word)
				.count();
		
		//end Kavitha trials
		*/
		final KTable<Windowed<String, Long> wordCounts = textLines
				// Split each text line, by whitespace, into words.  The text lines are the record
				// values, i.e. we can ignore whatever data is in the record keys and thus invoke
				// `flatMapValues()` instead of the more generic `flatMap()`.
				.flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
				// Count the occurrences of each word (record key).
				//
				// This will change the stream type from `KStream<String, String>` to `KTable<String, Long>`
				// (word -> count).  In the `count` operation we must provide a name for the resulting KTable,
				// which will be used to name e.g. its associated state store and changelog topic.
				//
				// Note: no need to specify explicit serdes because the resulting key and value types match our default serde settings
				.groupBy((key, word) -> word)
			.windowedBy(TimeWindows.of(TimeUnit.MINUTES.toMillis(1)))
				.count();

		// Write the `KTable<String, Long>` to the output topic.
		KStream<String, Long> wordCountsStream = wordCounts.toStream();
		wordCountsStream.to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

		return wordCountsStream;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaStreamsApplication.class, args);
	}
}
