package com.motorhome.controller.motorhome;

import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles logic of each Motorhome entity appearing in the Motorhome Menu
 * Author(s): Octavian Roman
 */
public class MotorhomeEntityController implements Initializable {

    @FXML private ImageView image;
    @FXML private Label brandLabel;
    @FXML private Label modelLabel;
    @FXML private Label typeLabel;
    @FXML private Label availabilityLabel;
    @FXML private MenuItem edit;
    @FXML private MenuItem delete;

    // This class is always going to insert the last entity inside the ArrayList.
    // The same index for brands and models since they get added simultaneously.
    public final int entityIndex = Session.motorhomeEntityList.size() - 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);
        Image i = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        image.setImage(i);
        brandLabel.setText(brand.getName());
        modelLabel.setText(model.getName());
        typeLabel.setText(motorhome.getType());
        availabilityLabel.setText(motorhome.isRented() ? "Rented" : "Available");
    }
}