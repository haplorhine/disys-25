package com.example.energyuser;

import com.example.energyuser.data.Producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

// diese klasse wird automatisch von spring erkannt und regelmäßig ausgeführt
@Component
public class TimedMessenger {

    private final RabbitTemplate rabbit;
    private final Random random = new Random();

    // spring fügt das rabbittemplate automatisch ein (dependency injection)
    // @autowired ist hier nicht nötig, weil es nur einen konstruktor gibt
    public TimedMessenger(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    // diese methode wird alle 2500 millisekunden (2,5 sekunden) automatisch aufgerufen
    @Scheduled(fixedRate = 2500)
    public void sendConsumer() {
        LocalDateTime now = LocalDateTime.now();
        double baseKwh = getBaseKwhForTime(now.toLocalTime());

        // erstelle ein neues nachrichten-objekt
        Producer msg = new Producer();
        msg.setType("USER");
        msg.setAssociation("COMMUNITY");
        msg.setKwh(baseKwh);
        msg.setDatetime(now);

        System.out.println("Sending consumer message" + msg);
        // schicke das objekt an die queue "usage_in"
        rabbit.convertAndSend("usage_in", msg);
    }
    // berechne stromverbrauch je nach tageszeit (morgen, abend, nacht, tagsüber)
    private double getBaseKwhForTime(LocalTime time) {
        int hour = time.getHour();

        if (hour >= 6 && hour <= 9) {
            // Morgenspitze
            return 0.001 + (0.002 * random.nextDouble());
        } else if (hour >= 17 && hour <= 21) {
            // Abendspitze
            return 0.0012 + (0.0025 * random.nextDouble());
        } else if (hour >= 0 && hour <= 5) {
            // Nacht: kaum Verbrauch
            return 0.0003 + (0.0005 * random.nextDouble());
        } else {
            // Tagesverbrauch normal
            return 0.0007 + (0.0012 * random.nextDouble());
        }
    }
}
