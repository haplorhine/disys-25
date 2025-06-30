package com.example.usageservice.repository;

import com.example.usageservice.data.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;

// repository-interface für datenbankzugriff auf hourlyusage-objekte
// erweitert jparepository, wodurch standard-methoden wie save(), findById(), deleteById() usw. automatisch bereitgestellt werden
// entity-klasse: hourlyusage, primärschlüssel-typ: localdatetime
public interface UsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {
}
