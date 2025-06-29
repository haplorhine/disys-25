package com.example.restapi;

import com.example.entity.CurrentPercentage;
import com.example.entity.Energy;
import com.example.entity.EnergyPercentage;
import com.example.entity.HourlyUsage;
import com.example.repository.CurrentPercentageRepository;
import com.example.repository.UsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REstController für die Ermittlung der Daten für die Anzeige
 */
@RestController
@RequestMapping("energy")
public class EnergyAPI {

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
    public EnergyAPI(CurrentPercentageRepository currentPercentageRepository, UsageRepository usageRepository) {

        this.currentPercentageRepository = currentPercentageRepository;
        this.usageRepository = usageRepository;


    }



    public static LocalDateTime truncateToHour(LocalDateTime input) {

        return input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);
    }

    /**
     * Ermitteln des aktuellen Erzeugungssstandes
     *
     * @return Energy-Percentage - Objekt mit den aktuellen Werten
     */
    @GetMapping("current")
    public ResponseEntity<EnergyPercentage>  getCurrent() {
        LocalDateTime nowHour = truncateToHour(LocalDateTime.now());
        return currentPercentageRepository.findById(nowHour)
                .map(cp -> ResponseEntity.ok(new EnergyPercentage(cp.getCommunityDepleted(), cp.getGridPortion())))
                .orElse(ResponseEntity.notFound().build());

    }

    /**
     * Ermitteln der historischen Daten lt start- und Endedatum.
     * Die Parameter werden als Parameter in der GetUrl übergeben.
     * Die Daten des Ausschnittes werden summiert.
     *
     * @param start - startdatum
     * @param end   - endatum
     * @return Energy.class - Objekt mit den summierten WErten.
     */
    @GetMapping("history")
    public Energy getHistory(@RequestParam(value = "start") LocalDateTime start, @RequestParam(value = "ende") LocalDateTime end) {

        Energy result = new Energy();
        List<HourlyUsage> hourlyUsages = usageRepository.findByHourTimeBetween(start, end);
        hourlyUsages.forEach(hourlyUsage ->
                result.add(hourlyUsage.getCommunityProduced(), hourlyUsage.getCommunityUsed(), hourlyUsage.getGridUsed()));
        return result;

    }

    /**
     * Ermitteln aller historischen Daten in einem Zeitabschnitt
     * Der Abschnitt wird wieder als Parameter in der Url agegeben.
     * Die Daten werden als HashMap <Zeit, Energy> zurück geliefert.
     *
     * @param start - beginn des gewünschten Ausschnitts
     * @param end   - Ende des gewünschten Ausschnitts
     * @return HashMap mit den Einzeldaten
     */
    @GetMapping("detail")
    public List<Energy> getDetailHistory(@RequestParam(value = "start") LocalDateTime start, @RequestParam(value = "ende") LocalDateTime end) {

        List<HourlyUsage> hourlyUsages = usageRepository.findByHourTimeBetween(start, end);
        return hourlyUsages.stream().map(hourlyUsage ->
                new Energy(hourlyUsage.getCommunityProduced(), hourlyUsage.getCommunityUsed(), hourlyUsage.getGridUsed(), hourlyUsage.getHourTime())).collect(Collectors.toList());


    }

    @GetMapping("getValidData")
    public List<LocalDateTime> getValidData() {

        List<HourlyUsage> usages = usageRepository.findAllByOrderByHourTimeAsc();
        return usages.stream().map(HourlyUsage::getHourTime).collect(Collectors.toList());
    }



}
