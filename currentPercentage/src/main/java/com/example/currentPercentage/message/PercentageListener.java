package com.example.currentPercentage.message;


import com.example.currentPercentage.data.PercentageMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    @RabbitListener(queues = "percentage_in")
    public void receive(PercentageMessage msg) {
        System.out.println("Empfangen in CurrentPercentageService:");
        System.out.println(msg.toString());

        // Hier kannst du die Daten z. B. weiterverarbeiten oder berechnen
    }
}
