package com.example.usageservice.data;

import java.time.LocalDateTime;

public class HourlyStats {
    private LocalDateTime hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public HourlyStats(LocalDateTime hour) {
        this.hour = hour;
    }

    public void addProduced(double kwh) {
        communityProduced += kwh;
    }

    public void addUsed(double kwh) {
        double restFromCommunity = Math.max(communityProduced - communityUsed, 0);
        double usedFromCommunity = Math.min(kwh, restFromCommunity);
        double usedFromGrid = kwh - usedFromCommunity;

        communityUsed += usedFromCommunity;
        gridUsed += usedFromGrid;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    @Override
    public String toString() {
        return String.format("Hour: %s | Produced: %.3f | Used: %.3f | Grid: %.3f",
                hour, communityProduced, communityUsed, gridUsed);
    }
}

