package com.motorhome.model;

/**
 * Model class of Motorhome entity
 * Author(s): Octavian Roman
 */
public class Motorhome {

    // Attributes
    private int id;
    private int model_id;
    private String image;
    private boolean rented;
    private String type;
    private int beds;

    // Constructors
    public Motorhome(int id, int model_id, String image, boolean rented, String type, int beds) {
        this.id = id;
        this.model_id = model_id;
        this.image = image;
        this.rented = rented;
        this.type = type;
        this.beds = beds;
    }
    // ID-less for entity generation (we don't know the ID until we actually insert into database)
    public Motorhome(int model_id, String image, String type, int beds) {
        this.model_id = model_id;
        this.image = image;
        this.type = type;
        this.beds = beds;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getModel_id() {
        return model_id;
    }

    public String getImage() {
        return image;
    }

    public boolean isRented() {
        return rented;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setModel_id(int model_id) {
        this.model_id = model_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }
}
