package com.motorhome.controller;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML HBox header;
    @FXML Button tempButton;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bridge.setMainMenuController(this);
        tempButton.setOnAction(event -> {
            FXUtils.changeScene(event, "authentication", "NMH Authentication", false, false, "authentication", 400, 500);
        });
    }
}