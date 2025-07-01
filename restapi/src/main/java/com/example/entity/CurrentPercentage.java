package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
// jpa-entity für current_percentage
// kennzeichnet diese klasse als datenbanktabelle (wird von spring/jpa verwaltet)
// beim speichern wird automatisch ein datensatz in der tabelle angelegt oder aktualisiert
@Entity
@Table(name = "current_percentage")
public class CurrentPercentage {
    // primärschlüssel: stundenzeitpunkt
    @Id
    @Column(name = "hour_time", nullable = false)
    private LocalDateTime id;
    // wie viel prozent der erzeugten energie verbraucht wurden (max 100)
    @Column(name = "community_depleted")
    private Double communityDepleted;
    // wie viel prozent des verbrauchs aus dem öffentlichen netz kamen
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