package com.motorhome.controller.rental;

import com.motorhome.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class RentalMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        backButton.setOnAction(actionEvent -> FXUtils.changeRoot("main_menu","main_menu", backButton));
    }
}
