package com.motorhome.model;

/**
 * Model class of Model entity
 * Author(s): Octavian Roman
 */
public class Model {
    // Attributes
    private int id;
    private int brand_id;
    private String name;
    private double price;

    // Constructors
    public Model(int id, int brand_id, String name, double price) {
        this.id = id;
        this.brand_id = brand_id;
        this.name = name;
        this.price = price;
    }
    // ID-less for entity generation (we don't know the ID until we actually insert into database)
    public Model(String name, float price) {
        this.name = name;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
