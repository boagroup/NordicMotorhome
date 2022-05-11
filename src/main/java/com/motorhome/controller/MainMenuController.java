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

public class MainMenuController implements Initializable {

    @FXML HBox header;
    @FXML Label usernameLabel;
    @FXML ImageView userImage;
    @FXML Button logoutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bridge.setMainMenuController(this);
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        logoutButton.setOnAction(actionEvent -> {
            FXUtils.changeScene(actionEvent, "authentication", "NMH Authentication", false, false, "authentication", 400, 500);
            Session.CurrentUser.unloadUserDetails();
        });
    }
}