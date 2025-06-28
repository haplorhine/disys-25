package com.example.usageservice.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "hourly_usage")
public class HourlyUsage {
    @Id
    @Column(name = "hour_time", nullable = false)
    private Instant id;

    @Column(name = "community_produced")
    private Double communityProduced;

    @Column(name = "community_used")
    private Double communityUsed;

    @Column(name = "grid_used")
    private Double gridUsed;

    public Instant getId() {
        return id;
    }

    public void setId(Instant id) {
        this.id = id;
    }

    public Double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(Double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public Double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(Double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public Double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(Double gridUsed) {
        this.gridUsed = gridUsed;
    }

    @Override
    public String toString() {
        return "HourlyUsage{" +
                "id=" + id +
                ", communityProduced=" + communityProduced +
                ", communityUsed=" + communityUsed +
                ", gridUsed=" + gridUsed +
                '}';
    }
}