package com.example.currentPercentage.repository;

import com.example.currentPercentage.entities.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

// repository-interface für datenbankzugriff auf currentpercentage-objekte
// erweitert jparepository, wodurch standard-methoden wie save(), findById(), deleteById() usw. automatisch bereitgestellt werden
// entity-klasse: currentpercentage, primärschlüssel-typ: localdatetime
public interface CurrentPercentaceRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}
