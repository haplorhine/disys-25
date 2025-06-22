package com.example.scheduled;

import com.example.scheduled.data.Producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimedMessenger {

    private final RabbitTemplate rabbit;

    public TimedMessenger(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    //sendet alle 5 Sekunden Daten an usage_in
    @Scheduled(fixedRate = 5000)
    public void sendProducer() {
        System.out.println("Sending message...");

        Producer producerMessage = new Producer();
        rabbit.convertAndSend("usage_in", producerMessage);
    }


}
