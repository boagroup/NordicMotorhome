package com.motorhome.controller.main;

import com.motorhome.utilities.FXUtils;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Authentication Controller
 * Handles logic of authentication
 * Author(s): Octavian Roman
 */
public class AuthenticationController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Button signInButton;
    @FXML private Label errorLabel;

    private static PreparedStatement preparedStatement = null;
    private static Connection connection = null;

    private void login(Event event, String username, String password) {
        // If either of the two fields, or both, are empty
        if (username.equals("") || password.equals("")) {
            // Display fields empty error
            errorLabel.setText("All fields are mandatory");
            errorLabel.setVisible(true);
        } else {
            // If both fields are not empty
            try {
                // Connect to the database
                connection = Database.getConnection();
                if (connection != null) {
                    // Prepare statement which selects password associated with unique username
                    preparedStatement = connection.prepareStatement("SELECT AES_DECRYPT(password, ?) AS password FROM users WHERE username = ?");
                }
                // Execute statement with entry from field
                preparedStatement.setString(1, System.getProperty("key"));
                preparedStatement.setString(2, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                // If the result of the execution is empty
                if (!resultSet.isBeforeFirst()) {
                    // Display user not found error
                    errorLabel.setText("Specified user not found");
                    errorLabel.setVisible(true);
                } else {
                        // If the result is not empty, move the cursor to the result
                        resultSet.next();
                        // Check if passwords match
                        if (resultSet.getString("password").equals(password)) {
                            // Success scenario, log user in
                            Session.CurrentUser.loadUserDetails(username);
                            FXUtils.changeScene(event, "main_menu", "Nordic Motorhomes", true, true, "main_menu",  -1, -1);
                        } else {
                            // Display password incorrect error
                            errorLabel.setText("Password does not match");
                            errorLabel.setVisible(true);
                        }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.closeConnection(connection);
             }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signInButton.setOnAction(event -> {
            login(event, userField.getText(), passwordField.getText());
            // Reset fields after button press; otherwise entries will persist even on logout
            userField.setText("");
            passwordField.setText("");
        });

        // ====REMOVE THIS BEFORE DEPLOYMENT==== //

        // This is a shortcut to login as "admin" by pressing "Enter" for development purposes
        rootPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                login(keyEvent, "admin", "admin");
                userField.setText("");
                passwordField.setText("");

            }
        });
        // ====REMOVE THIS BEFORE DEPLOYMENT==== //

        // Make error label invisible if any field is being modified
        userField.setOnKeyTyped(event -> {
            if (errorLabel.isVisible()) {
                errorLabel.setVisible(false);
            }
        });
        passwordField.setOnKeyTyped(event -> {
            if (errorLabel.isVisible()) {
                errorLabel.setVisible(false);
            }
        });
    }
}