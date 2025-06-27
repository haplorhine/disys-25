package com.example.usageservice.message;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.repository.UsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.usageservice.data.Producer;

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

        messageProducer.setTime(new Timestamp(System.currentTimeMillis()));
        Timestamp timestamp = new Timestamp(messageProducer.getTime().getTime());
        HourlyUsage usage = usageRepository.findByDateAndHourNative(timestamp);

        if (usage == null) {
            usage = new HourlyUsage();
            usage.setId(truncateToInstantHour(messageProducer.getTime()));
        }


        if (messageProducer.getAssocation().equals("PRODUCER")) {

            usage.setCommunityUsed(usage.getCommunityProduced() + messageProducer.getEnergy());
        } else {
            
        }
        usageRepository.save(usage);

    }

    public static Instant truncateToInstantHour(Date input) {
        LocalDateTime ldt = input.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);

        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

}
