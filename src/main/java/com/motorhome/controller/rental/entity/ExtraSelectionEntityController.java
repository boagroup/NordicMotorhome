package com.motorhome.controller.rental.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Extra;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the pop-up which allows selecting extras.
 * Used both by RentalAddController and RentalEditController.
 * Author(s): Octavian Roman
 */
public class ExtraSelectionEntityController implements Initializable {
    // FX Nodes
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private CheckBox selectedCheckbox;

    // This class is always going to insert the last entity inside the ArrayList.
    public final int entityIndex = Session.extraEntityList.size() - 1;
    // True if controller is triggered by add, false if by edit
    public static boolean adding;

    /**
     * Provides reactivity to the application by displaying the changes in price as the user selects or de-selects extras.
     * Uses a boolean to know whether it's called by Add or Edit.
     * @param extra Extra object representing the entity that is being selected/deselected.
     */
    private void registerCheckBoxChangesToArrayList(Extra extra) {
        if (selectedCheckbox.isSelected()) {
            Session.extraSelectionList.add(extra);
        } else Session.extraSelectionList.removeIf(e -> e.getId() == extra.getId());
        if (adding) {
            Bridge.getRentalAddController().reflectFieldChanges();
        } else Bridge.getRentalEditController().reflectFieldChanges();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Extra extra = Session.extraEntityList.get(entityIndex);
        nameLabel.setText(extra.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(extra.getPrice()) + " €");
        // Check those extras which the entity already has selected previously
        for (Extra e : Session.extraSelectionList) {
            if (e.getId() == extra.getId()) {
                selectedCheckbox.setSelected(true);
            }
        }
        selectedCheckbox.setOnAction(actionEvent -> registerCheckBoxChangesToArrayList(extra));
    }
}