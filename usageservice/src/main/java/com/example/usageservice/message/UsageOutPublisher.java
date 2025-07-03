package com.example.usageservice.message;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.data.PercentageMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

// diese klasse wird von spring als service verwaltet
// sie berechnet prozentwerte aus stundenwerten und sendet diese an die queue "percentage_in"
@Service
public class UsageOutPublisher {

    private final RabbitTemplate rabbit;

    public UsageOutPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    // berechnet prozentuale werte aus den usage-daten und sendet sie als message
    public void publish(HourlyUsage stats) {
        double produced = stats.getCommunityProduced(); // strom, der produziert wurde
        double used = stats.getCommunityUsed();         // strom, der verbraucht wurde
        double grid = stats.getGridUsed();              // strom aus dem öffentlichen netz

        double communityDepleted;
        if (produced == 0.0) {
            // kein strom erzeugt – community musste alles aus netz nehmen
            communityDepleted = 100.0;
        } else {
            // wie viel prozent der erzeugten energie verbraucht wurden
            communityDepleted = Math.min(100.0, (used / produced) * 100.0);
        }

        double gridPortion;
        if (used == 0.0) {
            gridPortion = 0.0;
        } else {
            gridPortion = (grid / (grid + used)) * 100.0;
        }

        // nachricht erzeugen und mit gerundeten werten füllen
        PercentageMessage message = new PercentageMessage();
        message.setHour(stats.getHour());
        message.setCommunityDepleted(round(communityDepleted));
        message.setGridPortion(round(gridPortion));

        // nachricht an die queue "percentage_in" senden
        rabbit.convertAndSend("percentage_in", message);
        System.out.println("UsageOutPublisher gesendet: " + message);
    }

    // rundet einen double-wert auf zwei nachkommastellen
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
