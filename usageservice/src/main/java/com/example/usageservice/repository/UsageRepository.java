package com.example.usageservice.repository;

import com.example.usageservice.data.HourlyUsage;
import com.example.usageservice.data.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface UsageRepository extends JpaRepository<HourlyUsage, Instant> {

    @Query(value = "SELECT e.* FROM hourly_usage e " +
            "WHERE DATE(e.hour_time) = DATE(:ts) " +
            "AND EXTRACT(HOUR FROM e.hour_time) = EXTRACT(HOUR FROM CAST(:ts AS timestamp))", nativeQuery = true)
    HourlyUsage findByDateAndHourNative(@Param("ts") Timestamp ts);
}
