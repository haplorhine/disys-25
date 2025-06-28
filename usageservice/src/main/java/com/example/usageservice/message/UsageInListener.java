package com.example.usageservice.message;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.data.Producer;
import com.example.usageservice.repository.UsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class UsageInListener {
    private final RabbitTemplate rabbit;
    private final UsageRepository usageRepository;
    public UsageInListener(RabbitTemplate rabbit, UsageRepository usageRepository) {
        this.rabbit = rabbit;
        this.usageRepository = usageRepository;
    }

    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {

        System.out.println(messageProducer.toString());
        //rabbit.convertAndSend("echo_in", message);

        Timestamp timestamp = Timestamp.valueOf(messageProducer.getDatetime());
        HourlyUsage usage =
                usageRepository.findById(truncateToInstantHour(messageProducer.getDatetime())).orElse(null);

        if (usage == null) {
            usage = new HourlyUsage();
            usage.setId(truncateToInstantHour(messageProducer.getDatetime()));
            usage.setCommunityUsed(Double.valueOf(0));
            usage.setCommunityProduced(Double.valueOf(0));
            usage.setGridUsed(Double.valueOf(0));
        }


        System.out.println(usage.toString());
        if (messageProducer.getType().equals("PRODUCER")) {

            Double newProduced =usage.getCommunityProduced() + messageProducer.getKwh();
            usage.setCommunityProduced(newProduced);
            System.out.println("setze new Produced: " + newProduced);




        } else {

            // ermitteln den offenen Anteil für GridUsed - ist noch ein Anteil Verfügbar
            Double gridUsed = usage.getCommunityProduced() - usage.getCommunityUsed();


            Double newCommunityUsed = usage.getCommunityUsed()  +
                    Math.min(messageProducer.getKwh(),gridUsed);

            Double newGridUsed = usage.getGridUsed() + messageProducer.getKwh() - gridUsed;
            usage.setCommunityUsed(newCommunityUsed);
            usage.setGridUsed(newGridUsed);
            System.out.println("setze new CommunityUsed: " + newCommunityUsed);
            System.out.println("setze new GridUsed: " + newGridUsed);
            
        }
        usageRepository.saveAndFlush(usage);

    }

    public static Instant truncateToInstantHour(LocalDateTime input) {
        LocalDateTime ldt = input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);

        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

}
