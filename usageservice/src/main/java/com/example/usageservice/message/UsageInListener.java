package com.example.usageservice.message;

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

    private final UsageOutPublisher usageOutPublisher;

    public UsageInListener(UsageOutPublisher usageOutPublisher) {
        this.usageOutPublisher = usageOutPublisher;
    }

    @RabbitListener(queues = "usage_in")
    public void receiveMessage(Producer messageProducer) {
        System.out.println("Empfangen: " + messageProducer);

        usageOutPublisher.sendDummyPercentageMessage();
    }
}
