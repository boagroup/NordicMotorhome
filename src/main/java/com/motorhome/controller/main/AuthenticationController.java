package com.motorhome.controller.main;

import com.motorhome.persistence.DataResult;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import com.motorhome.utilities.FXUtils;
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

    private static final Database db = Database.getInstance();

    private enum Results {
        NOT_ENOUGH_FIELDS,
        USER_NOT_FOUND,
        SUCCESS,
        WRONG_PASSWORD,
        FAILURE
    }

    private Results fetch(String username, String password) {
        if (username.equals("") || password.equals("")) { return Results.NOT_ENOUGH_FIELDS; }

        // get passwords of users with same username
        //language=SQL
        String query = "SELECT AES_DECRYPT(password, ?) AS password FROM users WHERE username = ?";
        DataResult results = db.executeQuery(query, System.getProperty("key"), username);

        if (results == null) { return Results.FAILURE; }
        if (results.isEmpty()) { return Results.USER_NOT_FOUND; }

        // convert password from bytes to string
        String pass = new String((byte[]) results.getData(0,0));
        // check if password is correct
        if (pass.equals(password)) { return Results.SUCCESS; }
        return Results.WRONG_PASSWORD;
    }

    private void login(Event event, String username, String password) {
        switch (fetch(username, password)){
            case NOT_ENOUGH_FIELDS -> {
                errorLabel.setText("All fields are mandatory");
                errorLabel.setVisible(true);
            }
            case USER_NOT_FOUND -> {
                errorLabel.setText("Specified user not found");
                errorLabel.setVisible(true);
            }
            case SUCCESS -> {
                Session.CurrentUser.loadUserDetails(username);
                FXUtils.changeScene(event, "main_menu", "Nordic Motorhomes", true, true, "main_menu",  -1, -1);
            }
            case WRONG_PASSWORD -> {
                errorLabel.setText("Password does not match");
                errorLabel.setVisible(true);
            }
            case FAILURE -> {
                errorLabel.setText("ERROR: can't connect to the database!");
                errorLabel.setVisible(true);
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