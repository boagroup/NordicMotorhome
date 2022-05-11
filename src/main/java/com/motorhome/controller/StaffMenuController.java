package com.motorhome.controller;

import com.motorhome.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffMenuController implements Initializable {

    @FXML Label usernameLabel;
    @FXML ImageView userImage;
    @FXML Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        backButton.setOnAction(actionEvent -> {
            FXUtils.changeScene(actionEvent, "main_menu", "NMH Main Menu", "main_menu");
        });
    }
}
