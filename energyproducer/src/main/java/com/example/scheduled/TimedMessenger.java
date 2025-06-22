package com.example.scheduled;

import com.example.scheduled.data.Producer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class TimedMessenger {

    private final RabbitTemplate rabbit;
    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper();

    public TimedMessenger(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    //sendet alle 5 Sekunden Daten an usage_in
    @Scheduled(fixedRate = 5000)
    public void sendProducer() {
        System.out.println("Sending message...");

        double sunshineFactor = getCurrentSunshineFactor();
        double baseKwh = 0.002 + (0.001 * random.nextDouble());  // 0.002 - 0.003 kWh
        double adjustedKwh = baseKwh * sunshineFactor;



        Producer msg = new Producer();
        msg.setType("PRODUCER");
        msg.setAssociation("COMMUNITY");
        msg.setKwh(adjustedKwh);
        msg.setDatetime(LocalDateTime.now());

        System.out.println(" → " + msg);
        rabbit.convertAndSend("usage_in", msg);
    }
    private double getCurrentSunshineFactor() {
        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=48.2&longitude=16.37&hourly=sunshine_duration&timezone=Europe%2FVienna";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode times = root.at("/hourly/time");
            JsonNode sunshine = root.at("/hourly/sunshine_duration");

            String now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            for (int i = 0; i < times.size(); i++) {
                if (times.get(i).asText().equals(now)) {
                    int sunshineSeconds = sunshine.get(i).asInt();
                    return Math.min(sunshineSeconds / 3600.0, 1.0);  // max 1.0
                }
            }

        } catch (Exception e) {
            System.out.println("Fehler bei Wetter-API, verwende sunshineFactor = 0.5");
        }

        return 0.5; // fallback
    }
}
