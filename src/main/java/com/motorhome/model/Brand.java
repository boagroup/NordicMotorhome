package com.motorhome.model;

/**
 * Model class of Brand entity
 * Author(s): Octavian Roman
 */
public class Brand {
    // Attributes
    private int id;
    private String name;
    private double price;

    // Constructors
    public Brand(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
