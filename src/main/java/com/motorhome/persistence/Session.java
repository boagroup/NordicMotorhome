package com.motorhome.persistence;

import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Staff;
import com.motorhome.model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class that can take care of session data.
 * Here, the currently logged-in user is stored.
 * Likewise, entity data can be stored here to reflect changes to data during the session.
 * Author(s): Octavian Roman
 */
public class Session {

    /**
     * Inner Singleton storing currently logged-in User.
     * Can be used anywhere to dynamically update the UI, restrict access to functionality, etc.
     * Cannot use inheritance because it contains attributes from two entities (User & Staff).
     * Interfaces would work, but then the solution would be worse because it requires two interfaces,
     * which won't even be used to their full potential as CurrentUser only requires specific attributes, not all.
     */
    public static class CurrentUser {

        private String username, firstname, lastname, image;
        private Boolean admin;
        private int staff_ID;

        private static CurrentUser currentUser = new CurrentUser();

        // Constructor (private for Singleton)
        private CurrentUser() {}

        // Getters
        public static CurrentUser getCurrentUser() {
            return currentUser;
        }

        public String getUsername() {
            return username;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public String getImage() {
            return image;
        }

        public Boolean getAdmin() {
            return admin;
        }

        public int getStaff_ID() {
            return staff_ID;
        }

        // Setters
        public void setUsername(String username) {
            this.username = username;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setAdmin(Boolean admin) {
            this.admin = admin;
        }

        public void setStaff_ID(int staff_ID) {
            this.staff_ID = staff_ID;
        }

        /**
         * Load the singleton with the relevant details of the user currently logged in.
         * Used in the success scenario of the AuthenticationController.login() function.
         * @param username Unique username of the user to be loaded into the singleton.
         */
        public static void loadUserDetails(String username) {
            currentUser = CurrentUser.getCurrentUser();
            Connection connection = SimpleDatabase.getConnection();
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT firstName, lastName, image, admin, staff_ID FROM staff INNER JOIN users ON staff.id = users.staff_id WHERE username = ?");
                    preparedStatement.setString(1, username);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    currentUser.setUsername(username);
                    currentUser.setFirstname(resultSet.getString("firstName"));
                    if ((resultSet.getString("lastName") != null)) {
                        currentUser.setLastname(resultSet.getString("lastName"));
                    } else currentUser.setLastname("");
                    currentUser.setAdmin(resultSet.getBoolean("admin"));
                    currentUser.setImage(resultSet.getString("image"));
                    currentUser.setStaff_ID(resultSet.getInt("staff_ID"));
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    SimpleDatabase.closeConnection(connection);
                }
            }
        }

        /**
         * Unload the singleton.
         * Just to be safe, shouldn't really be needed.
         */
        public static void unloadUserDetails() {
            currentUser.setUsername("");
            currentUser.setFirstname("");
            currentUser.setLastname("");
            currentUser.setAdmin(false);
            currentUser.setImage("");
            currentUser.setStaff_ID(0);
        }

    }

    public static ArrayList<Staff> staffEntityList = new ArrayList<>();
    public static ArrayList<User> userEntityList = new ArrayList<>();

    public static ArrayList<Brand> brandEntityList = new ArrayList<>();
    public static ArrayList<Model> modelEntityList = new ArrayList<>();
}
