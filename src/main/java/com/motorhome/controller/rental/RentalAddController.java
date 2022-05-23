package com.motorhome.controller.rental;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class RentalAddController implements Initializable {

    @FXML
    private Button addExtrasButton;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField distanceTextField;

    @FXML
    private DatePicker endDateDatePicker;

    @FXML
    private ImageView image;

    @FXML
    private TextArea notes;

    @FXML
    private Button pickMotorhomeButton;

    @FXML
    private TextField pickupTextField;

    @FXML
    private ChoiceBox<?> seasonChoiceBox;

    @FXML
    private DatePicker startDateDatePicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
