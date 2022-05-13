package com.motorhome.controller;

import com.motorhome.model.Staff;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StaffEntityController implements Initializable {

    @FXML private ImageView image;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label genderLabel;
    @FXML private ImageView options;

    private final int entityIndex = Session.StaffEntityList.size() - 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Staff staff = Session.StaffEntityList.get(entityIndex);
        Image i = new Image(Objects.requireNonNull(getClass().getResource(staff.getImage())).toExternalForm());
        image.setImage(i);
        nameLabel.setText(staff.getFirstName() + " " + staff.getLastName());
        roleLabel.setText(staff.getRole());
        telephoneLabel.setText(staff.getTelephone());
        genderLabel.setText(staff.getGender());
        if (!staff.getGender().equals("")) {
            switch (staff.getGender()) {
                case "M" -> genderLabel.setText("Male");
                case "F" -> genderLabel.setText("Female");
                case "N" -> genderLabel.setText("Non-Binary");
                case "D" -> genderLabel.setText("Decline to State");
            }
        }
    }
}