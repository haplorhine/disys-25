package com.example;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@SpringBootApplication // macht aus der klasse eine spring boot app
@EnableScheduling // erlaubt zeitgesteuerte methoden (z.b. mit @scheduled)
public class RestapiApplication {

	// startpunkt der anwendung
	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}



}
