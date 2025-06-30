package com.example.currentPercentage.message;


import com.example.currentPercentage.data.PercentageMessage;
import com.example.currentPercentage.entities.CurrentPercentage;
import com.example.currentPercentage.repository.CurrentPercentaceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

// service-klasse, die auf nachrichten aus der queue "percentage_in" hört
// speichert empfangene prozentwerte (netzanteil, community-depletion) in der datenbank
@Service
public class PercentageListener {

    private final CurrentPercentaceRepository  currentPercentaceRepository;;

    // konstruktor-injection: spring übergibt automatisch die abhängigkeiten
    // currentPercentaceRepository: datenbankzugriff auf current_percentage-daten
    public PercentageListener(CurrentPercentaceRepository currentPercentaceRepository) {
        this.currentPercentaceRepository = currentPercentaceRepository;
    }

    // wird automatisch aufgerufen, wenn eine nachricht in "percentage_in" eingeht
    @RabbitListener(queues = "percentage_in")
    public void receive(PercentageMessage msg) {
        System.out.println("Empfangen in CurrentPercentageService:");
        System.out.println(msg.toString());

        // versuche, existierenden datensatz für die stunde zu finden
        CurrentPercentage currentPercentage = currentPercentaceRepository.findById(msg.getHour()).orElse(null);

        // wenn kein datensatz vorhanden ist, neuen eintrag erstellen
        if(currentPercentage == null) {
            currentPercentage = new CurrentPercentage();;
            currentPercentage.setId(msg.getHour());
        }
        // aktuelle werte setzen
        currentPercentage.setGridPortion(msg.getGridPortion());
        currentPercentage.setCommunityDepleted(msg.getCommunityDepleted());
        // speichern (neuer oder aktualisierter eintrag)
        currentPercentaceRepository.save(currentPercentage);
    }
}
