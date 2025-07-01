package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
// jpa-entity für stündliche energiewerte
// kennzeichnet diese klasse als datenbanktabelle (wird von spring/jpa verwaltet)
// beim speichern wird automatisch ein datensatz in der tabelle angelegt oder aktualisiert
@Entity
public class HourlyUsage {
    // primärschlüssel: jede zeile steht für eine bestimmte stunde
    @Id
    @Column(name="hour_time")
    private LocalDateTime hourTime;

    // wie viel strom in dieser stunde produziert wurde
    @Column(name="community_produced")
    private double communityProduced;

    // wie viel strom intern verbraucht wurde
    @Column(name="community_used")
    private double communityUsed;

    // wie viel strom aus dem öffentlichen netz bezogen wurde
    @Column(name="grid_used")
    private double gridUsed;

    public HourlyUsage() {

    }
    public HourlyUsage(LocalDateTime hour) {
        this.hourTime = hour;
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

    public LocalDateTime getHourTime() {
        return hourTime;
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

    public void setHour_time(LocalDateTime hour_time) {
        this.hourTime = hour_time;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    @Override
    public String toString() {
        return String.format("Hour: %s | Produced: %.3f | Used: %.3f | Grid: %.3f",
                hourTime, communityProduced, communityUsed, gridUsed);
    }
}

