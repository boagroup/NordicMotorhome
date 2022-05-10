package com.motorhome.controller;

import com.motorhome.Utilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML Button tempButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tempButton.setOnAction(event -> Utilities.changeScene(
                event, "authentication", "NMH Authentication", false, false, 400, 500
        ));
    }
}