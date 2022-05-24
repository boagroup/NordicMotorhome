package com.motorhome.model;

import java.sql.Date;

/**
 * Model class of Rental entity
 * Author(s): Octavian Roman
 */
public class Rental {
    // Attributes
    private int id;
    private int motorhome_id;
    private String state;
    private String location;
    private int distance;
    private String season;
    private Date start_date;
    private Date end_date;
    private double final_price;
    private String notes;

    // Constructors
    public Rental(int id, int motorhome_id, String state, int distance, String location, String season, Date start_date, Date end_date, double final_price, String notes) {
        this.id = id;
        this.motorhome_id = motorhome_id;
        this.state = state;
        this.distance = distance;
        this.location = location;
        this.season = season;
        this.start_date = start_date;
        this.end_date = end_date;
        this.final_price = final_price;
        this.notes = notes;
    }
    // ID-less
    public Rental(int motorhome_id, String state, int distance, String location, String season, Date start_date, Date end_date, double final_price, String notes) {
        this.motorhome_id = motorhome_id;
        this.state = state;
        this.distance = distance;
        this.location = location;
        this.season = season;
        this.start_date = start_date;
        this.end_date = end_date;
        this.final_price = final_price;
        this.notes = notes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getMotorhome_id() {
        return motorhome_id;
    }

    public String getState() {
        return state;
    }

    public int getDistance() {
        return distance;
    }

    public String getLocation() {
        return location;
    }

    public String getSeason() {
        return season;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public double getFinal_price() {
        return final_price;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMotorhome_id(int motorhome_id) {
        this.motorhome_id = motorhome_id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setFinal_price(double final_price) {
        this.final_price = final_price;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
