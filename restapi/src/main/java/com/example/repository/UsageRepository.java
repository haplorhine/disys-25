package com.example.repository;

import com.example.entity.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {

    List<HourlyUsage> findByHourTimeBetween(LocalDateTime start, LocalDateTime end);

    List<HourlyUsage> findAllByOrderByHourTimeAsc();

}
