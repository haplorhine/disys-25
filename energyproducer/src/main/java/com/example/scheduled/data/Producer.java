package com.example.scheduled.data;

import java.util.Date;

public class Producer  {

    private String type;
    private String assocation;
    private Float energy;

    private Date time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssocation() {
        return assocation;
    }

    public void setAssocation(String assocation) {
        this.assocation = assocation;
    }

    public Float getEnergy() {
        return energy;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
