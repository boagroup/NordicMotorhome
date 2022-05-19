package com.motorhome.model;

/**
 * Model class of Staff entity
 * Author(s): Octavian Roman
 */
public class Staff {

    // Attributes
    private int id;
    private String firstName;
    private String lastName;
    private String image;
    private String telephone;
    private String role;
    private String gender;

    // Constructors
    public Staff(int id, String firstName, String lastName, String image, String telephone, String role, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.telephone = telephone;
        this.role = role;
        this.gender = gender;
    }

    // ID-less for entity generation (we don't know the ID until we actually insert into database)
    public Staff(String firstName, String lastName, String image, String telephone, String role, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.telephone = telephone;
        this.role = role;
        this.gender = gender;
    }
    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getImage() {
        return image;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
