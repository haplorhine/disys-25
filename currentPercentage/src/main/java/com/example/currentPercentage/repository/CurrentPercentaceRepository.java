package com.example.currentPercentage.repository;

import com.example.currentPercentage.entities.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentaceRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}
