package com.example.messagesender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimedMessenger {

    private final RabbitTemplate rabbit;

    public TimedMessenger(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedRate = 5000)
    public void sendProducer() {
        System.out.println("Sending message...");
        rabbit.convertAndSend("producer_in", "Hello World!");
    }

    @Scheduled(fixedRate = 2500)
    public void sendConsumer() {
        System.out.println("Sending consumer message...");
        rabbit.convertAndSend("consumer_in", "Hello World!");
    }
}
