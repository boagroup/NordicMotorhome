package com.motorhome.controller.motorhome.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller that handles the logic behind each model entity in the Motorhome Settings Pop-Up
 * Author(s): Jakub Patelski
 */
public class ModelEntityController implements Initializable {
    // FX Nodes
    @FXML private Label brandLabel;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private VBox remove;

    // This class is always going to insert the last entity inside the ArrayList.
    public final int entityIndex = Session.brandEntityList.size() - 1;

    /**
     * Function that removes a given model entity from the database
     * @param model object representing the entity to be deleted
     */
    private void remove(Model model) {
        // Get confirmation with an alert
        Alert alert = FXUtils.confirmDeletion("Model", nameLabel.getText());
        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                // Prepare statement
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "DELETE FROM models WHERE id = ?"
                );
                preparedStatement.setInt(1, model.getId());
                preparedStatement.execute();
                // If removal is successful, use the Bridge to clear and re-fetch the models to reflect changes
                Bridge.getMotorhomeSettingsController().refreshModels();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.closeConnection(connection);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model model = Session.modelEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        nameLabel.setText(model.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(model.getPrice()) + " €");
        brandLabel.setText(brand.getName());
        remove.setOnMouseClicked(mouseEvent -> remove(model));
    }
}
