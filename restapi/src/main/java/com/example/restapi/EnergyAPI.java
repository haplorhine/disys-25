package com.example.restapi;

import com.example.entity.Energy;
import com.example.entity.EnergyPercentage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("energy")
public class EnergyAPI {

    private Map<String, Energy> data;

    public EnergyAPI() {
        data = new LinkedHashMap<>();
        Map<String, Integer> archiveData = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {

            archiveData = getArchiveWeatherData(startDate, LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime dateTime = date.atTime(hour, 0);
                double percentage = archiveData.get(dateTime.format(formatter)) / 3600.;

                Energy currentEnergy = new Energy(20. * percentage, 20. * percentage, 4.);
                data.put(dateTime.format(formatter), currentEnergy);
            }
        }

    }

    public static <K, V> Map<K, V> getSubMapByKeyRange(Map<K, V> inputMap, K startKey, K endKey) {
        Map<K, V> result = new LinkedHashMap<>();

        boolean inRange = false;
        for (Map.Entry<K, V> entry : inputMap.entrySet()) {
            if (entry.getKey().equals(startKey)) {
                inRange = true;
            }

            if (inRange) {
                result.put(entry.getKey(), entry.getValue());
            }

            if (entry.getKey().equals(endKey)) {
                break;
            }
        }

        return result;
    }

    @GetMapping("current")
    public EnergyPercentage getCurrent() {

        return new EnergyPercentage(10.3, 4.4);

    }

    @GetMapping("history")
    public Energy getHistory(@RequestParam(value = "start") String start, @RequestParam(value = "ende") String end) {

        Energy result = new Energy();
        getSubMapByKeyRange(data, start, end).forEach((key, value) -> result.add(value));
        return result;

    }

    @GetMapping("detail")
    public Map<String, Energy> getDetailHistory(@RequestParam(value = "start") String start, @RequestParam(value = "ende") String end) {


        return getSubMapByKeyRange(data, start, end);


    }

    private Map<String, Integer> getArchiveWeatherData(LocalDate start, LocalDate end) throws IOException, InterruptedException {

        Map<String, Integer> result = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String url = "https://archive-api.open-meteo.com/v1/archive" +
                "?latitude=48.2" +
                "&longitude=16.37" +
                "&start_date=" + formatter.format(start) +
                "&end_date=" + formatter.format(end) +
                "&hourly=sunshine_duration" +
                "&timezone=Europe/Vienna";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        JsonNode times = root.at("/hourly/time");
        JsonNode sunshine = root.at("/hourly/sunshine_duration");

        for (int i = 0; i < times.size(); i++) {
            String time = times.get(i).asText().replace("T", " ");
            int seconds = sunshine.get(i).asInt();
//            double minutes = seconds / 60.0;
//            System.out.printf("%s → %.1f Minuten Sonnenschein%n", time, minutes);
            result.put(time, seconds);
        }

        return result;
    }
}
