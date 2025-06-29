package com.example.repository;


import com.example.entity.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {
}
