package com.example.repository;

import com.example.entity.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
// repository-interface für datenbankzugriff auf hourlyusage-objekte
// erweitert jparepository, wodurch standard-methoden wie save(), findById(), deleteById() usw. automatisch bereitgestellt werden
// entity-klasse: hourlyusage, primärschlüssel-typ: localdatetime
public interface UsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {
    // - findByHourTimeBetween(...): gibt alle einträge im angegebenen zeitraum zurück
    // spring data jpa erstellt die passende datenbankabfrage automatisch – basierend auf dem methodennamen
    // z.B. findByHourTimeBetween(...) → entspricht SQL: SELECT ... WHERE hour_time BETWEEN ... AND ...
    List<HourlyUsage> findByHourTimeBetween(LocalDateTime start, LocalDateTime end);
    List<HourlyUsage> findByHourTimeBetweenOrderByHourTimeDesc(LocalDateTime start, LocalDateTime end);
    // - findAllByOrderByHourTimeAsc(): gibt alle einträge aufsteigend nach stunde sortiert zurück
    // spring data jpa erstellt die passende datenbankabfrage automatisch – basierend auf dem methodennamen
    // z.B. findByHourTimeBetween(...) → entspricht SQL: SELECT ... WHERE hour_time BETWEEN ... AND ...
    List<HourlyUsage> findAllByOrderByHourTimeAsc();

}
