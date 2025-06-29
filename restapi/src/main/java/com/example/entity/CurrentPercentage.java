package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentage {
    @Id
    @Column(name = "hour_time", nullable = false)
    private LocalDateTime id;

    @Column(name = "community_depleted")
    private Double communityDepleted;

    @Column(name = "grid_portion")
    private Double gridPortion;

    public LocalDateTime getId() {
        return id;
    }

    public void setId(LocalDateTime id) {
        this.id = id;
    }

    public Double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(Double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public Double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(Double gridPortion) {
        this.gridPortion = gridPortion;
    }

}