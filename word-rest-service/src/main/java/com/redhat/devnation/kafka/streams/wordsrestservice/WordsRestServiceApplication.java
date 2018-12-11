package com.redhat.devnation.kafka.streams.wordsrestservice;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WordsRestServiceApplication {

	@Bean
	public RouteBuilder routeBuilder() {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {

				routeBuilder()
						.restConfiguration("servlet")
						.bindingMode(RestBindingMode.auto);

				rest("/lines")
						.post()
						.route()
						.log("${in.headers}")
						.inOnly("kafka:streams-plaintext-input?brokers={{camel.kafka.bootstrap.servers}}")
						.endRest();

			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(WordsRestServiceApplication.class, args);
	}
}
