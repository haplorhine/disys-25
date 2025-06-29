package com.example.usageservice.repository;

import com.example.usageservice.data.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;

public interface UsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {


}
