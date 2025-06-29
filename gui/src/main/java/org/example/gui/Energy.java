package org.example.gui;

import java.time.LocalDateTime;

public class Energy {
    private Double communityProduced =0.;
    private Double communityUsed =0.;
    private Double gridUsed = 0.;

    private LocalDateTime timeHour = LocalDateTime.now();

    public LocalDateTime getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(LocalDateTime timeHour) {
        this.timeHour = timeHour;
    }

    public Energy() {

    }

    public Energy(Double communityProduced, Double communityUsed, Double gridUsed, LocalDateTime timeHour) {
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
        this.timeHour = timeHour;
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


}