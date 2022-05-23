package com.motorhome.model;

/**
 * Model class of Extra entity
 * Author(s): Octavian Roman
 */
public class Client {

    // Attributes
    private int id;
    private int rental_id;
    private String firstName;
    private String lastName;
    private String telephone;

    // Constructors
    public Client(int id, int rental_id, String firstName, String lastName, String telephone) {
        this.id = id;
        this.rental_id = rental_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
    }
    // ID-less
    public Client(int rental_id, String firstName, String lastName, String telephone) {
        this.rental_id = rental_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRental_id() {
        return rental_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTelephone() {
        return telephone;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRental_id(int rental_id) {
        this.rental_id = rental_id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
