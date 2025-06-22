package com.example.usageservice.message;

import com.example.usageservice.data.HourlyStats;
import com.example.usageservice.service.UsageStorageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.usageservice.data.Producer;

/*
@Service
public class UsageInListener {
    private final RabbitTemplate rabbit;
    public UsageInListener(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {

        System.out.println(messageProducer.toString());
        //rabbit.convertAndSend("echo_in", message);


    }

}
*/
@Service
public class UsageInListener {

    private final UsageStorageService storageService;
    private final UsageOutPublisher usageOutPublisher;

    public UsageInListener(UsageStorageService storageService, UsageOutPublisher usageOutPublisher) {
        this.storageService = storageService;
        this.usageOutPublisher = usageOutPublisher;
    }

    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {
        if (messageProducer == null || messageProducer.getDatetime() == null || messageProducer.getType() == null) {
            System.err.println("Ungültige Nachricht empfangen (wird ignoriert): " + messageProducer);
            return;
        }

        System.out.println("Empfangen: " + messageProducer);
        HourlyStats stats = storageService.processMessage(messageProducer);
        usageOutPublisher.publish(stats);
    }
}
