package com.example.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerMessageIn {

    private final RabbitTemplate rabbit;

    public ProducerMessageIn(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }


    @RabbitListener(queues = "producer_in")
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");

    }





}
