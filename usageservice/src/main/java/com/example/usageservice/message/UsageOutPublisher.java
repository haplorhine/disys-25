package com.example.usageservice.message;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.data.PercentageMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UsageOutPublisher {

    private final RabbitTemplate rabbit;

    public UsageOutPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publish(HourlyUsage stats) {
        double produced = stats.getCommunityProduced();
        double used = stats.getCommunityUsed();
        double grid = stats.getGridUsed();

        double communityDepleted;
        if (produced == 0.0) {
            // Kein Strom produziert – Community ist komplett leer
            communityDepleted = 100.0;
        } else {
            communityDepleted = Math.min(100.0, (used / produced) * 100.0);
        }

        double gridPortion;
        if (used == 0.0) {
            gridPortion = 0.0;
        } else {
            gridPortion = (grid / used) * 100.0;
        }

        PercentageMessage message = new PercentageMessage();
        message.setHour(stats.getHour());
        message.setCommunityDepleted(round(communityDepleted));
        message.setGridPortion(round(gridPortion));

        rabbit.convertAndSend("percentage_in", message);
        System.out.println("UsageOutPublisher gesendet: " + message);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
