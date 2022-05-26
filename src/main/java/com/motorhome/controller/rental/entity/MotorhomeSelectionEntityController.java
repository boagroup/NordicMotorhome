package com.motorhome.controller.rental.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the pop-up that facilitates motorhome selection.
 * Author(s): Octavian Roman
 */
public class MotorhomeSelectionEntityController implements Initializable {
    // FX Nodes
    @FXML private ImageView image;
    @FXML private Label brandLabel;
    @FXML private Label modelLabel;
    @FXML private Label typeLabel;
    @FXML private HBox entityContainer;
    // This class is always going to insert the last entity inside the ArrayList.
    public final int entityIndex = Session.motorhomeEntityList.size() - 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);
        brandLabel.setText(brand.getName());
        modelLabel.setText(model.getName());
        typeLabel.setText(motorhome.getType());
        Image i = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        image.setImage(i);
        entityContainer.setOnMouseClicked(mouseEvent -> {
            // Bridge to the Rental Add or Edit Controllers and pass in the proper ID.
            Bridge.getRentalAddController().pickMotorhome(motorhome.getId());
            // Bridge to the Motorhome Selection Controller and close its window since we just selected something.
            Bridge.getRentalMotorhomeSelectionController().closeSelectionWindow();
        });
    }
}