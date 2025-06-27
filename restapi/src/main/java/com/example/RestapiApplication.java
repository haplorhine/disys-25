package com.example;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@SpringBootApplication
@EnableScheduling
public class RestapiApplication {

	@Bean
	public Queue producer_in() {
		return new Queue("producer_in", true);
	}

	@Bean
	public Queue consumer_in() {
		return new Queue("consumer_in", true);
	}


	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}



}
