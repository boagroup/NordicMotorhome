package com.motorhome.controller.rental;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Extra;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ExtraSelectionEntityController implements Initializable {

    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private CheckBox selectedCheckbox;

    public final int entityIndex = Session.extraEntityList.size() - 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Extra extra = Session.extraEntityList.get(entityIndex);
        nameLabel.setText(extra.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(extra.getPrice()) + " €");
        selectedCheckbox.setOnAction(actionEvent -> {
            if (selectedCheckbox.isSelected()) {
                Bridge.getRentalAddController().extraArrayList.add(extra);
            } else Bridge.getRentalAddController().extraArrayList.remove(extra);
            Bridge.getRentalAddController().reflectFieldChanges();
        });
    }
}