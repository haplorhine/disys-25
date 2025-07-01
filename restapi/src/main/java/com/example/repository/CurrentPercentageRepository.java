package com.example.repository;


import com.example.entity.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
// repository-interface für datenbankzugriff auf currentpercentage-objekte
// erweitert jparepository, wodurch standard-methoden wie save(), findById(), deleteById() usw. automatisch bereitgestellt werden
// entity-klasse: currentpercentage, primärschlüssel-typ: localdatetime
public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}
