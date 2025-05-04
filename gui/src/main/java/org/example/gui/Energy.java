package org.example.gui;

public class Energy {
    private Double communityProduced;
    private Double communityUsed;

    private Double gridUsed;

    public Energy() {
        communityProduced= (double) 0;
        communityUsed= (double) 0;
        gridUsed= (double) 0;

    }

    public Energy(Double communityProduced, Double communityUsed, Double gridUsed) {
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
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

    public void add(Energy addElem) {

    }
}
