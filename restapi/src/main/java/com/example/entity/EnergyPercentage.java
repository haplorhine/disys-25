package com.example.entity;

/**
 * Diese Klasse dient der Anzeige der aktuellen Daten
 */
public class EnergyPercentage {
    private Double currentCommunityPool;
    private Double gridPorton;

    public EnergyPercentage(Double currentCommunityPool, Double gridPorton) {
        this.currentCommunityPool = currentCommunityPool;
        this.gridPorton = gridPorton;
    }

    public Double getCurrentCommunityPool() {
        return currentCommunityPool;
    }

    public void setCurrentCommunityPool(Double currentCommunityPool) {
        this.currentCommunityPool = currentCommunityPool;
    }

    public Double getGridPorton() {
        return gridPorton;
    }

    public void setGridPorton(Double gridPorton) {
        this.gridPorton = gridPorton;
    }
}
