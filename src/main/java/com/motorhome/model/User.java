package com.motorhome.model;

/**
 * Model class of User entity
 * Author(s): Octavian Roman
 */
public class User {
    // Attributes
    private int id;
    private int staff_id;
    private String username;
    private String password;
    private boolean admin;

    // Constructors
    public User(int id, int staff_id, String username, String password, boolean admin) {
        this.id = id;
        this.staff_id = staff_id;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    // ID-less for entity generation (we don't know the ID until we actually insert into database)
    public User(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getStaff_id() {
        return staff_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStaff_id(int staff_id) {
        this.staff_id = staff_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


}
