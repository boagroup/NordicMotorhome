package com.motorhome.controller.staff;

import com.motorhome.FXUtils;
import com.motorhome.model.Staff;
import com.motorhome.model.User;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
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
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles edition of Staff and User entities.
 * Author(s): Octavian Roman
 */
public class StaffEditController implements Initializable {

    // Variable gets changed if the user selects an image, defaults to placeholder.
    String imageName = "/assets/users/user_placeholder.png";

    // Integer that gets changed to the appropriate value every time the user clicks on the "Edit" option of an entity
    // -1 to get exception if something goes wrong instead of unwanted entity
    public static int entityIndex = -1;

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
        if (!selectedFile.exists()) {
            return;
        }
        // Generate random file name with date-timestamp + file extension and assign it to imageName
        imageName = FXUtils.generateRandomImageName(selectedFile);
        String imagePath = "/assets/users/" + imageName;

        // Prepare paths for both destinations. DOES NOT WORK IN JAR
        String pathToNewImage = "src/main/resources/assets/users/" + imageName;
        String pathToCompiledImages = "target/classes/assets/users/" + imageName;

        // Copy images to aforementioned paths
        FXUtils.copyImageToAssets(selectedFile, pathToNewImage);
        FXUtils.copyImageToAssets(selectedFile, pathToCompiledImages);

        // Set ImageView inside pop-up to reflect picked image. Won't appear if image hasn't been added to target
        Image newImage = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());
        imageName = imagePath;
        previewImage.setImage(newImage);
    }

    /**
     * Populates the FX nodes of the edit window with the information corresponding to the entity.
     * @param staff Staff entity that is being edited.
     * @param user User entity associated with the Staff
     */
    private void loadDataIntoFields(Staff staff, User user) {
        firstName.setText(staff.getFirstName());
        lastName.setText(staff.getLastName());
        Image image = new Image(Objects.requireNonNullElse(getClass().getResource(staff.getImage()),
                getClass().getResource("/assets/users/user_placeholder.png")).toExternalForm());
        previewImage.setImage(image);
        role.setText(staff.getRole());
        telephone.setText(staff.getTelephone());
        switch (staff.getGender()) {
            case "M" -> gender.setValue("Male");
            case "F" -> gender.setValue("Female");
            case "N" -> gender.setValue("Non-Binary");
            case "D" -> gender.setValue("Decline to State");
        }
        username.setText(user.getUsername());
        password.setText(user.getPassword());
        admin.setSelected(user.isAdmin());
    }

    /**
     * Takes a Staff and User object and sets it to the values provided in the FX nodes.
     * @param staff Staff object to be updated
     * @param user User object to be updated
     */
    private void updateEntities(Staff staff, User user) {
        staff.setFirstName(firstName.getText());
        staff.setLastName(lastName.getText());
        staff.setImage(imageName);
        staff.setRole(role.getText());
        staff.setTelephone(telephone.getText());
        switch (gender.getValue()) {
            case "Male" -> staff.setGender("M");
            case "Female" -> staff.setGender("F");
            case "Non-Binary" -> staff.setGender("N");
            case "Decline to State", "Gender" -> staff.setGender("D");
        }
        user.setUsername(username.getText());
        user.setPassword(password.getText());
        user.setAdmin(admin.isSelected());
    }

    /**
     * Update CurrentUser Singleton to reflect changes if the user updates themselves
     * Doesn't update current scene, might fix later
     * @param staff Staff object whose values will be used to update the Singleton
     * @param user User object whose values will be used to update the Singleton
     */
    private void updateCurrentUser(Staff staff, User user) {
        if (staff.getId() == Session.CurrentUser.getCurrentUser().getStaff_ID()) {
            Session.CurrentUser.getCurrentUser().setFirstname(staff.getFirstName());
            Session.CurrentUser.getCurrentUser().setLastname(staff.getLastName());
            Session.CurrentUser.getCurrentUser().setImage(staff.getImage());
            Session.CurrentUser.getCurrentUser().setAdmin(user.isAdmin());
        }
    }

    /**
     * Takes a Staff and User entity and updates the data corresponding to the ID of the entities.
     * Also encrypts password with AES.
     * @param staff Staff object whose attributes will be taken to update the row
     * @param user User object whose attributes will be taken to update the row
     * @return true if successful, false otherwise
     */
    private boolean editStaff(Staff staff, User user) {
        // Establish connection
        Connection connection = SimpleDatabase.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "UPDATE staff JOIN users ON staff.id = users.staff_id " +
                        "SET firstName = ?, lastName = ?, image = ?, telephone = ?, role = ?, gender = ?, " +
                        "username = ?, password = AES_ENCRYPT(?,?), admin = ? " +
                        "WHERE staff.id = ?");

            preparedStatement.setString(1, staff.getFirstName().equals("") ? null : staff.getFirstName());
            preparedStatement.setString(2, staff.getLastName());
            preparedStatement.setString(3, staff.getImage());
            preparedStatement.setString(4, staff.getTelephone());
            preparedStatement.setString(5, staff.getRole());
            preparedStatement.setString(6, staff.getGender());
            preparedStatement.setString(7, user.getUsername());
            preparedStatement.setString(8, user.getPassword());
            preparedStatement.setString(9, System.getProperty("key"));
            preparedStatement.setBoolean(10, user.isAdmin());
            preparedStatement.setInt(11, staff.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            SimpleDatabase.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the Staff and User objects corresponding to the entity on screen from their ArrayLists
        Staff staff = Session.staffEntityList.get(entityIndex);
        User user = Session.userEntityList.get(entityIndex);
        imageName = staff.getImage();
        // Set image picker
        previewImage.setOnMouseClicked(mouseEvent -> pickImage());
        // Set gender actions onto Node
        setGenderActions();
        // Put data from retrieved entities into the fields
        loadDataIntoFields(staff, user);
        // Update the entities with the user changes and query the database on submit
        submit.setOnAction(actionEvent -> {
            updateEntities(staff, user);
            // If something went wrong throw error
            if (!editStaff(staff, user)) {
                FXUtils.alert(Alert.AlertType.ERROR, "Something went wrong! Check for errors in the fields.", "Staff Edit", "Edit failed!", true);
            }
            updateCurrentUser(staff, user);
            // Close the edit window
            Stage stage = (Stage) submit.getScene().getWindow();
            stage.close();
        });
    }
}