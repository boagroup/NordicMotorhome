package com.motorhome.controller;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main Menu Controller
 * Handles logic of Main Menu
 * Author(s): Octavian Roman
 */
public class MainMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button logoutButton;
    @FXML private HBox rentalsMenu;
    @FXML private HBox motorhomesMenu;
    @FXML private HBox staffMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bridge.setMainMenuController(this);
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        rentalsMenu.setOnMouseClicked(mouseEvent ->
                FXUtils.changeScene(mouseEvent, "rental_menu", "NMH Rentals Menu","rental_menu"));

        motorhomesMenu.setOnMouseClicked(mouseEvent ->
                FXUtils.changeScene(mouseEvent, "motorhome_menu", "NMH Motorhomes Menu","motorhome_menu"));

        staffMenu.setOnMouseClicked(mouseEvent -> {
            if (Session.CurrentUser.getCurrentUser().getAdmin()) {
                FXUtils.changeScene(mouseEvent, "staff_menu", "NMH Staff Menu","staff_menu");
            } else {
                FXUtils.alert(Alert.AlertType.ERROR, "You do not have permission to access this menu.", "Staff Menu", "Unauthorized Access", true);
            }
        });

        logoutButton.setOnAction(actionEvent -> {
            FXUtils.changeScene(actionEvent, "authentication", "NMH Authentication", false, false, "authentication", 400, 500);
            Session.CurrentUser.unloadUserDetails();
        });
    }
}