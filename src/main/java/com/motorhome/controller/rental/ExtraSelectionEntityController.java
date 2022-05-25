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
    // True if controller is triggered by add, false if by edit
    public static boolean adding;

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
        Bridge.setExtraSelectionEntityController(this);
        Extra extra = Session.extraEntityList.get(entityIndex);
        nameLabel.setText(extra.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(extra.getPrice()) + " €");
        for (Extra e : Session.extraSelectionList) {
            if (e.getId() == extra.getId()) {
                selectedCheckbox.setSelected(true);
            }
        }
        selectedCheckbox.setOnAction(actionEvent -> registerCheckBoxChangesToArrayList(extra));
    }
}