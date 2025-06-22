package com.example.usageservice.service;

import com.example.usageservice.data.HourlyStats;
import com.example.usageservice.data.Producer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsageStorageService {

    private final Map<LocalDateTime, HourlyStats> storage = new ConcurrentHashMap<>();

    public HourlyStats processMessage(Producer message) {
        // Stunde runden → z. B. 14:34 → 14:00
        LocalDateTime hour = message.getDatetime().truncatedTo(ChronoUnit.HOURS);

        // Falls noch kein Eintrag: neuen Stats-Eintrag für diese Stunde anlegen
        storage.putIfAbsent(hour, new HourlyStats(hour));
        HourlyStats stats = storage.get(hour);

        // Nachrichtstyp auswerten
        if ("PRODUCER".equalsIgnoreCase(message.getType())) {
            stats.addProduced(message.getKwh());
        } else if ("USER".equalsIgnoreCase(message.getType())) {
            stats.addUsed(message.getKwh());
        }
        System.out.println("Aktuelle HourlyStats:");
        storage.forEach((key, value) -> System.out.println(value));
        return stats;
    }

    public HourlyStats getStatsForHour(LocalDateTime hour) {
        return storage.get(hour);
    }

    // Optional für Debug
    public Map<LocalDateTime, HourlyStats> getAllStats() {
        return storage;
    }
}
