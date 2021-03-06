package com.motorhome.controller.main;

import com.motorhome.utilities.FXUtils;
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
    // FX Nodes
    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button logoutButton;
    @FXML private HBox rentalsMenu;
    @FXML private HBox motorhomesMenu;
    @FXML private HBox staffMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        rentalsMenu.setOnMouseClicked(mouseEvent ->
                FXUtils.changeRoot("rental_menu", "rental_menu", rentalsMenu));

        motorhomesMenu.setOnMouseClicked(mouseEvent ->
                FXUtils.changeRoot("motorhome_menu", "motorhome_menu", motorhomesMenu));

        staffMenu.setOnMouseClicked(mouseEvent -> {
            if (Session.CurrentUser.getCurrentUser().getAdmin()) {
                FXUtils.changeRoot("staff_menu", "staff_menu", staffMenu);
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