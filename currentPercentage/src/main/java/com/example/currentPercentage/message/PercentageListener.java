package com.example.currentPercentage.message;


import com.example.currentPercentage.data.PercentageMessage;
import com.example.currentPercentage.entities.CurrentPercentage;
import com.example.currentPercentage.repository.CurrentPercentaceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PercentageListener {

    private final CurrentPercentaceRepository  currentPercentaceRepository;;

    public PercentageListener(CurrentPercentaceRepository currentPercentaceRepository) {
        this.currentPercentaceRepository = currentPercentaceRepository;
    }

    @RabbitListener(queues = "percentage_in")
    public void receive(PercentageMessage msg) {
        System.out.println("Empfangen in CurrentPercentageService:");
        System.out.println(msg.toString());

        CurrentPercentage currentPercentage = currentPercentaceRepository.findById(msg.getHour()).orElse(null);

        if(currentPercentage == null) {

            currentPercentage = new CurrentPercentage();;
            currentPercentage.setId(msg.getHour());

        }

        currentPercentage.setGridPortion(msg.getGridPortion());
        currentPercentage.setCommunityDepleted(msg.getCommunityDepleted());
        currentPercentaceRepository.save(currentPercentage);

    }
}
