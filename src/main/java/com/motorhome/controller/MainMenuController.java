package com.motorhome.controller;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    @FXML Label usernameLabel;
    @FXML ImageView userImage;
    @FXML Button logoutButton;
    @FXML HBox rentalsMenu;
    @FXML HBox motorhomesMenu;
    @FXML HBox staffMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bridge.setMainMenuController(this);
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        rentalsMenu.setOnMouseClicked(mouseEvent -> {
            FXUtils.changeScene(mouseEvent, "rental_menu", "NMH Rentals Menu","rental_menu");
        });

        motorhomesMenu.setOnMouseClicked(mouseEvent -> {
            FXUtils.changeScene(mouseEvent, "motorhome_menu", "NMH Motorhomes Menu","motorhome_menu");
        });

        staffMenu.setOnMouseClicked(mouseEvent -> {
            FXUtils.changeScene(mouseEvent, "staff_menu", "NMH Staff Menu","staff_menu");
        });

        logoutButton.setOnAction(actionEvent -> {
            FXUtils.changeScene(actionEvent, "authentication", "NMH Authentication", false, false, "authentication", 400, 500);
            Session.CurrentUser.unloadUserDetails();
        });
    }
}