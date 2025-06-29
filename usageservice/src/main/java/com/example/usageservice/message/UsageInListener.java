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

@Service
public class UsageInListener {
    private final RabbitTemplate rabbit;
    private final UsageRepository usageRepository;

    private final UsageOutPublisher usageOutPublisher;


    public UsageInListener(RabbitTemplate rabbit, UsageRepository usageRepository, UsageOutPublisher usageOutPublisher) {
        this.rabbit = rabbit;
        this.usageRepository = usageRepository;


    this.usageOutPublisher = usageOutPublisher;
    }


    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {

        System.out.println(messageProducer.toString());
        //rabbit.convertAndSend("echo_in", message);

        Timestamp timestamp = Timestamp.valueOf(messageProducer.getDatetime());
        HourlyUsage usage =
                usageRepository.findById(truncateToHour(messageProducer.getDatetime())).orElse(null);

        if (usage == null) {
            usage = new HourlyUsage();
            usage.setHour_time(truncateToHour(messageProducer.getDatetime()));
            usage.setCommunityUsed(0);
            usage.setCommunityProduced(0);
            usage.setGridUsed(0);
        }


        System.out.println(usage.toString());
        if (messageProducer.getType().equals("PRODUCER")) {

        usage.addProduced(messageProducer.getKwh());


        } else {

            usage.addUsed(messageProducer.getKwh());
            
        }
        usageRepository.saveAndFlush(usage);

        usageOutPublisher.publish(usage);
    }

    public static LocalDateTime truncateToHour(LocalDateTime input) {

        return input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);
    }

}
