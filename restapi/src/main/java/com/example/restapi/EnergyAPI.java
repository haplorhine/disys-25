package com.example.restapi;

import com.example.entity.CurrentPercentage;
import com.example.entity.Energy;
import com.example.entity.EnergyPercentage;
import com.example.entity.HourlyUsage;
import com.example.repository.CurrentPercentageRepository;
import com.example.repository.UsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REstController für die Ermittlung der Daten für die Anzeige
 */
@RestController
@RequestMapping("energy")
public class EnergyAPI {

    private Map<String, Energy> data;

    private final CurrentPercentageRepository currentPercentageRepository;
    private final UsageRepository usageRepository;

    /**
     * Initialisieren der Daten
     * Derzeit noch alles im Speicher, wird danach von der Datenbank abgelöst
     * Es wird eine LinkedHashMap mit den Zeiten erzeugt
     * Key: Stunden
     * Values: Energy - hält die erzeugten Werte
     * Schon in Vorbereitung wird ober openWeatherData die Sonnenscheinzeit ermittelt um "realistische" daten zu haben
     */
    public EnergyAPI(CurrentPercentageRepository currentPercentageRepository,  UsageRepository usageRepository) {

        this.currentPercentageRepository = currentPercentageRepository;
        this.usageRepository = usageRepository;
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

                Energy currentEnergy = new Energy(20. * percentage, 10. * percentage, 5. * percentage, dateTime.format(formatter));
                data.put(dateTime.format(formatter), currentEnergy);
            }
        }

    }

    /**
     * ermitteln der Daten für den Zeitausschnitt gewählt wurde
     * derzeit wird noch über die HashMap gearbeitet - wird durch einen Datenbankzugriff abgelöst
     * @param inputMap - Daten
     * @param startKey - Startdatum
     * @param endKey - Enddatum
     * @return Map
     * @param <K> Uhrzeit
     * @param <V> Energy
     */
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

    /**
     * Ermitteln des aktuellen Erzeugungssstandes
     *
     * @return Energy-Percentage - Objekt mit den aktuellen Werten
     */
    @GetMapping("current")
    public EnergyPercentage getCurrent() {

        CurrentPercentage currentPercentage = currentPercentageRepository.findById(truncateToHour(LocalDateTime.now())).orElse(null);
        return new EnergyPercentage(currentPercentage.getCommunityDepleted(), currentPercentage.getGridPortion());

    }

    /**
     * Ermitteln der historischen Daten lt start- und Endedatum.
     * Die Parameter werden als Parameter in der GetUrl übergeben.
     * Die Daten des Ausschnittes werden summiert.
     * @param start - startdatum
     * @param end - endatum
     * @return Energy.class - Objekt mit den summierten WErten.
     */
    @GetMapping("history")
    public Energy getHistory(@RequestParam(value = "start") String start, @RequestParam(value = "ende") String end) {

        Energy result = new Energy();
        getSubMapByKeyRange(data, start, end).forEach((key, value) -> result.add(value));
        return result;

    }

    /**
     * Ermitteln aller historischen Daten in einem Zeitabschnitt
     * Der Abschnitt wird wieder als Parameter in der Url agegeben.
     * Die Daten werden als HashMap <Zeit, Energy> zurück geliefert.
     * @param start - beginn des gewünschten Ausschnitts
     * @param end - Ende des gewünschten Ausschnitts
     * @return HashMap mit den Einzeldaten
     */
    @GetMapping("detail")
    public Map<String, Energy> getDetailHistory(@RequestParam(value = "start") String start, @RequestParam(value = "ende") String end) {


        return getSubMapByKeyRange(data, start, end);


    }

    @GetMapping("getValidData")
    public List<LocalDateTime> getValidData() {

        List<HourlyUsage> usages = usageRepository.findAll();
        return usages.stream().map(HourlyUsage::getHour).collect(Collectors.toList());
    }

    /**
     * Ermitteln der historischen Wetterdaten über die APIi von open-meteo.com
     * Es wird auch der gewünschte Zeitraum gelesen. Es ist nicht möglich den Einzelwert für die Stunde
     * zu ermitteln. Die API liefert nur den Wert für einen ganzen Tag
     * @param start - Beginn des Ausschnitts
     * @param end - Ende des Ausschnitts
     * @return - Map mit Uhrzeit und Sonnenscheindauer
     * @throws IOException
     * @throws InterruptedException
     */
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

    public static LocalDateTime truncateToHour(LocalDateTime input) {

        return input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);
    }


}
