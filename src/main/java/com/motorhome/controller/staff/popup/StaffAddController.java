package com.motorhome.controller.staff.popup;

import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Staff;
import com.motorhome.model.User;
import com.motorhome.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Handles creation of Staff and User entities.
 * Author(s): Octavian Roman
 */
public class StaffAddController implements Initializable {
    // FX Nodes
    @FXML private ImageView previewImage;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField role;
    @FXML private TextField telephone;
    @FXML private ChoiceBox<String> gender;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private CheckBox admin;
    @FXML private Button submit;

    // Variable gets changed if the user selects an image, defaults to placeholder.
    private String imageName = "user_placeholder.png";

    /**
     * Adds options to Gender ChoiceBox.
     * ChoiceBox cannot be filled in SceneBuilder, hence why we run this on initialization.
     */
    private void setGenderActions() {
        gender.getItems().addAll("Male", "Female", "Non-Binary", "Decline to State");
        gender.setValue("Gender");
    }

    /**
     * Image picker.
     * Picks an image, generates random name for it, and puts it in assets/users/ both in compiled and source directories.
     * BROKEN IN JAR (STILL)
     * Last error to fix is being able to point to assets/users both in src/main and target in JAR files.
     * Can't find how to point to directory inside JAR, only files.
     */
    private void pickImage() {
        File selectedFile = FXUtils.imagePicker(previewImage);
        // Stop execution if no file is selected
        if (selectedFile == null) {
            return;
        }
        // Generate random file name with date-timestamp + file extension and assign it to imageName
        imageName = FXUtils.generateUniqueImageName(selectedFile);

        // Prepare paths for both destinations. DOES NOT WORK IN JAR
        String pathToNewImage = "src/main/resources/assets/users/" + imageName;
        String pathToCompiledImages = "target/classes/assets/users/" + imageName;

        // Copy images to aforementioned paths
        FXUtils.copyImageToAssets(selectedFile, pathToNewImage);
        FXUtils.copyImageToAssets(selectedFile, pathToCompiledImages);

        // Set ImageView inside pop-up to reflect picked image. Won't appear if image hasn't been added to target
        Image newImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/users/" + imageName)).toExternalForm());
        previewImage.setImage(newImage);
    }

    /**
     * Inserts a given Staff object into the database as a field
     * Also adds a user associated to the staff entity
     * The user entity only grants access to the system if its fields are valid
     * @param staff Staff object to be added to the database
     * @param user User object to be added to the database (if any)
     * @return true if successful, false otherwise
     */
    private boolean addStaff(Staff staff, User user) {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            // Insert staff table entity
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO staff (firstName, lastName, image, telephone, role, gender) " +
                        "VALUES (?,?,?,?,?,?);"
            );
            // Throw error only if no first name provided
            preparedStatement.setString(1, (staff.getFirstName().equals("") ? null : staff.getFirstName()));
            preparedStatement.setString(2, staff.getLastName());
            preparedStatement.setString(3, staff.getImage());
            preparedStatement.setString(4, staff.getTelephone());
            preparedStatement.setString(5, staff.getRole());
            preparedStatement.setString(6, staff.getGender());
            preparedStatement.execute();

            // Save staff entity ID into local variable
            preparedStatement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int id = resultSet.getInt(1);
            // Insert user table entity
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (username, password, admin, staff_id) " +
                            "VALUES (?,AES_ENCRYPT(?,?),?,?);"
            );
            preparedStatement.setString(1, (user.getUsername().equals("") ? null : user.getUsername()));
            preparedStatement.setString(2, (user.getPassword().equals("") ? null : user.getPassword()));
            preparedStatement.setString(3, System.getProperty("key"));
            preparedStatement.setBoolean(4, user.isAdmin());
            preparedStatement.setInt(5, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Database.closeConnection(connection);
        }
    }

    /**
     * Generates Staff and User objects that will be added to the database
     * @return array with Staff at index 0 and User at index 1
     */
    private Object[] generateEntities() {
        // Transform gender String to Enum (still a String, but MySQL takes it)
        String genderEnum =  "";
        switch (gender.getValue()) {
            case "Male" -> genderEnum = "M";
            case "Female" -> genderEnum = "F";
            case "Non-Binary" -> genderEnum = "N";
            case "Decline To State", "Gender" -> genderEnum = "D";
        }
        // Instantiate objects
        Staff staff = new Staff(firstName.getText(), lastName.getText(), "/assets/users/" + imageName, telephone.getText(), role.getText(), genderEnum);
        User user = new User(username.getText(), password.getText(), admin.isSelected());

        // Add objects to array
        Object[] result = new Object[2];
        result[0] = staff;
        result[1] = user;

        // Reset imageName variable for next time a staff is added
        imageName = "user_placeholder.png";

        // Return array
        return result;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set image picker
        previewImage.setOnMouseClicked(mouseEvent -> pickImage());
        // Set gender actions
        setGenderActions();
        // Define submit functionality
        submit.setOnAction(actionEvent -> {
            // Generate entities in accordance with what has been inserted in the fields and store it in array.
            Object[] entityArray = generateEntities();
            Staff staff = (Staff) entityArray[0];
            User user = (User) entityArray[1];

            // Try to add staff, store whether successful or not in boolean
            boolean success = addStaff(staff, user);

            // Close Staff Add pop-up
            Stage stage = (Stage) submit.getScene().getWindow();
            stage.close();

            // Show error alert if unsuccessful
            if (!success) {
                FXUtils.alert(Alert.AlertType.ERROR,
                        "Check for errors in your fields and try again",
                        "Staff not added",
                        "Something went wrong!", true);
            }
        });
    }
}
