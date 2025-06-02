package com.example.restapi;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.example.messagesender")
public class RestapiApplication {

	@GetMapping("/hello")
	public String helloworld(
			@RequestParam final String name) {
		System.out.println("GET /hello name=" + name);
		return "Hello, " + name + "\n";
	}

	@PostMapping("/hello")
	public String helloworldPost(
			@RequestBody String body) {
		System.out.println("POST /hello params:\n" + body);
		HashMap<String, String> params = new HashMap<>();
		body.lines().forEach((v) -> params.put(v.split("=")[0], v.split("=")[1]));
		return "Hello, " + params.get("name") + "\n";
	}
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
