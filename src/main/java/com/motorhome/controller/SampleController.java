package com.motorhome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SampleController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
