package com.example.repository;

import com.example.entity.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {


}
