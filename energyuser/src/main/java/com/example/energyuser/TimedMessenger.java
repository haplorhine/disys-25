package com.example.energyuser;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimedMessenger {

    private final RabbitTemplate rabbit;

    public TimedMessenger(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }


    @Scheduled(fixedRate = 2500)
    public void sendConsumer() {
        System.out.println("Sending consumer message...");
        rabbit.convertAndSend("consumer_in", "Hello World!");
    }
}
