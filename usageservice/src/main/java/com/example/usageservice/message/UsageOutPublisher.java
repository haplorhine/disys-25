package com.example.usageservice.message;

import com.example.usageservice.data.PercentageMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageOutPublisher {

    private final RabbitTemplate rabbit;

    public UsageOutPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void sendDummyPercentageMessage() {
        PercentageMessage message = new PercentageMessage();
        message.setHour(LocalDateTime.of(2025, 1, 10, 14, 0));
        message.setCommunityDepleted(100.0);
        message.setGridPortion(5.63);

        rabbit.convertAndSend("percentage_in", message);

        System.out.println("Dummy-PercentageMessage versendet!");
    }
}

