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

// restcontroller: stellt daten per http bereit (z. b. im json-format für frontend oder andere systeme)
// spring erkennt diese klasse als controller, der auf anfragen reagieren kann
// @requestmapping("energy") legt die basis-url fest → alle methoden sind unter /energy erreichbar
@RestController
@RequestMapping("energy")
public class EnergyAPI {

    private final CurrentPercentageRepository currentPercentageRepository;
    private final UsageRepository usageRepository;

    // konstruktor-injection: spring übergibt automatisch die benötigten repository-objekte
    public EnergyAPI(CurrentPercentageRepository currentPercentageRepository, UsageRepository usageRepository) {
        this.currentPercentageRepository = currentPercentageRepository;
        this.usageRepository = usageRepository;
    }

    // rundet ein LocalDateTime-objekt auf volle stunden (z. b. 14:35 → 14:00)
    public static LocalDateTime truncateToHour(LocalDateTime input) {
        return input
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.HOURS);
    }

    // stellt den http-endpunkt GET /energy/current bereit
    // liefert den aktuellen prozentwert für die community:
    // - wie viel der erzeugten energie wurde verbraucht (communityDepleted)
    // - wie viel des verbrauchs kam aus dem stromnetz (gridPortion)
    // rückgabe: EnergyPercentage-objekt im json-format
    @GetMapping("current")
    public ResponseEntity<EnergyPercentage>  getCurrent() {
        LocalDateTime nowHour = truncateToHour(LocalDateTime.now());
        return currentPercentageRepository.findById(nowHour)
                .map(cp -> ResponseEntity.ok(new EnergyPercentage(cp.getCommunityDepleted(), cp.getGridPortion())))
                .orElse(ResponseEntity.notFound().build());

    }

    // liefert summierte energiewerte für einen bestimmten zeitraum
    // start und ende werden per url-parameter übergeben (z. b. /energy/history?start=...&ende=...)
    // die werte werden über alle stunden im zeitraum aufsummiert (produziert, verbraucht, netzbezug)
    @GetMapping("history")
    public Energy getHistory(@RequestParam(value = "start") LocalDateTime start, @RequestParam(value = "ende") LocalDateTime end) {
        Energy result = new Energy();
        // spring data jpa erstellt die passende datenbankabfrage automatisch – basierend auf dem methodennamen
        // z.B. findByHourTimeBetween(...) → entspricht SQL: SELECT ... WHERE hour_time BETWEEN ... AND ...
        List<HourlyUsage> hourlyUsages = usageRepository.findByHourTimeBetween(start, end);
        hourlyUsages.forEach(hourlyUsage ->
                result.add(hourlyUsage.getCommunityProduced(), hourlyUsage.getCommunityUsed(), hourlyUsage.getGridUsed()));
        return result;
    }

    // stellt den http-endpunkt GET /energy/history bereit
    // erwartet zwei url-parameter: start und ende (format z. b. 2025-06-30T14:00)
    // → beispielaufruf: /energy/history?start=2025-06-30T10:00&ende=2025-06-30T15:00
    // liefert detaillierte energiedaten pro stunde für einen bestimmten zeitraum
    // rückgabe ist eine liste von Energy-objekten mit zeitstempel und werten je stunde
    @GetMapping("detail")
    public List<Energy> getDetailHistory(@RequestParam(value = "start") LocalDateTime start, @RequestParam(value = "ende") LocalDateTime end) {
        // spring data jpa erstellt die passende datenbankabfrage automatisch – basierend auf dem methodennamen
        // z.B. findByHourTimeBetween(...) → entspricht SQL: SELECT ... WHERE hour_time BETWEEN ... AND ...
        List<HourlyUsage> hourlyUsages = usageRepository.findByHourTimeBetween(start, end);
        return hourlyUsages.stream().map(hourlyUsage ->
                new Energy(hourlyUsage.getCommunityProduced(), hourlyUsage.getCommunityUsed(), hourlyUsage.getGridUsed(), hourlyUsage.getHourTime())).collect(Collectors.toList());
    }

    // stellt den http-endpunkt GET /energy/getValidData bereit
    // liefert eine liste aller zeitstempel (stunden), zu denen energiedaten gespeichert wurden
    // rückgabe: liste mit stundenwerten im format LocalDateTime (z. B. 2025-06-30T14:00)
    @GetMapping("getValidData")
    public List<LocalDateTime> getValidData() {
        // spring data jpa erstellt die passende datenbankabfrage automatisch – basierend auf dem methodennamen
        // z.B. findByHourTimeBetween(...) → entspricht SQL: SELECT ... WHERE hour_time BETWEEN ... AND ...
        List<HourlyUsage> usages = usageRepository.findAllByOrderByHourTimeAsc();
        return usages.stream().map(HourlyUsage::getHourTime).collect(Collectors.toList());
    }
}
