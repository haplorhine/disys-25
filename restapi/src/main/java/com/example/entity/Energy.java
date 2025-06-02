package com.example.entity;

/**
 * Die Klasse hält die Werte für die erzeugte Energy der Community, wieviel die community wieder verbraucht hat und wieviel
 * and das Netz abgebeben wurde.
 */
public class Energy {
    private Double communityProduced =0.;
    private Double communityUsed =0.;
    private Double gridUsed = 0.;

    private String timeHour = "";

    public String getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(String timeHour) {
        this.timeHour = timeHour;
    }

    public Energy() {

    }

    public Energy(Double communityProduced, Double communityUsed, Double gridUsed, String timeHour) {
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

    public void add(Energy addElem) {
        this.communityProduced +=addElem.getCommunityProduced();
        this.gridUsed +=addElem.getGridUsed();
        this.communityUsed += addElem.getCommunityUsed();

    }
}
