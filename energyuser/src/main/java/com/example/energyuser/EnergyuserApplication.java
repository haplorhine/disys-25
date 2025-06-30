package com.example.energyuser;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // macht aus der klasse eine spring boot app
@EnableScheduling      // erlaubt zeitgesteuerte methoden (z.b. mit @scheduled)

public class EnergyuserApplication {


	public static void main(String[] args) {
		SpringApplication.run(EnergyuserApplication.class, args);
	}

	// legt eine neue queue mit dem namen "usage_in" an
	// true bedeutet: die warteschlange bleibt auch nach einem neustart von rabbitmq erhalten
	@Bean
	public Queue usage_in() {
		return new Queue("usage_in", true);
	}

	// wandelt java-objekte in json (z.B. Producer) um
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// w ird von spring bereitgestellt, um das arbeiten mit rabbitmq zu erleichtern
	// stellt methoden bereit, um nachrichten an warteschlangen zu senden
	// das bean wird von spring verwaltet und kann später per @autowired in anderen komponenten genutzt werden
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	// stellt die verbindung zu rabbitmq her
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
		factory.setUsername("guest");
		factory.setPassword("guest");
		return factory;
	}

}
