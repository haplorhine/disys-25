package com.example.usageservice.message;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.data.Producer;
import com.example.usageservice.repository.UsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

// diese klasse wird von spring als service verwaltet
// sie empfängt nachrichten aus der queue "usage_in" und verarbeitet diese
@Service
public class UsageInListener {
    private final RabbitTemplate rabbit;
    private final UsageRepository usageRepository;
    private final UsageOutPublisher usageOutPublisher;

    // konstruktor-injection: spring übergibt automatisch die abhängigkeiten
    // rabbittemplate: optional zum senden
    // usageRepository: datenbankzugriff auf HourlyUsage daten
    // usageOutPublisher: zum senden berechneter prozentwerte nach der verarbeitung
    public UsageInListener(RabbitTemplate rabbit, UsageRepository usageRepository, UsageOutPublisher usageOutPublisher) {
        this.rabbit = rabbit;
        this.usageRepository = usageRepository;
        this.usageOutPublisher = usageOutPublisher;
    }

    // diese methode wird automatisch aufgerufen, wenn eine nachricht in der queue "usage_in" ankommt
    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {

        System.out.println(messageProducer.toString());
        Timestamp timestamp = Timestamp.valueOf(messageProducer.getDatetime());

        // prüfe, ob es schon einen eintrag für die entsprechende stunde gibt
        HourlyUsage usage = usageRepository
                .findById(truncateToHour(messageProducer.getDatetime()))
                .orElse(null);

        // wenn kein eintrag vorhanden ist, neuen stunden-datensatz anlegen
        if (usage == null) {
            usage = new HourlyUsage();
            usage.setHour_time(truncateToHour(messageProducer.getDatetime()));
            usage.setCommunityUsed(0);
            usage.setCommunityProduced(0);
            usage.setGridUsed(0);
        }
        System.out.println(usage.toString());

        // unterscheide zwischen PRODUCER und USER und addiere die kwh entsprechend
        if (messageProducer.getType().equals("PRODUCER")) {
            usage.addProduced(messageProducer.getKwh());
        } else {
            usage.addUsed(messageProducer.getKwh());
        }
        // speichere den aktualisierten wert in der datenbank
        usageRepository.saveAndFlush(usage);
        usageOutPublisher.publish(usage);
    }

    // runde ein datetime-objekt auf volle stunden (z.B. 10:37 → 10:00)
    public static LocalDateTime truncateToHour(LocalDateTime input) {
        return input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);
    }
}
