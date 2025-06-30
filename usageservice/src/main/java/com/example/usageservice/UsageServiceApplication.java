package com.example.usageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication // macht aus der klasse eine spring boot app
public class UsageServiceApplication {

	// legt drei neue queues an
	// true bedeutet: die warteschlange bleibt auch nach einem neustart von rabbitmq erhalten
	@Bean
	public Queue producer_in() {
		return new Queue("producer_in", true);
	}
	@Bean
	public Queue consumer_in() {
		return new Queue("consumer_in", true);
	}
	@Bean
	public Queue percentage_in() {
		return new Queue("percentage_in", true);
	}

	public static void main(String[] args) {
		SpringApplication.run(UsageServiceApplication.class, args);
	}

	// wandelt java-objekte in json (z.B. Producer) um
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
