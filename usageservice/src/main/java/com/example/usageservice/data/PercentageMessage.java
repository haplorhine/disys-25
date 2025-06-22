package com.example.usageservice.data;

import java.time.LocalDateTime;

public class PercentageMessage {
    private LocalDateTime hour;
    private double communityDepleted;
    private double gridPortion;

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    @Override
    public String toString() {
        return "PercentageMessage{" +
                "hour=" + hour +
                ", communityDepleted=" + communityDepleted +
                ", gridPortion=" + gridPortion +
                '}';
    }
}
