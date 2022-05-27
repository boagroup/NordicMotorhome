package com.motorhome.controller.motorhome.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Brand;
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
 * Controller that handles the logic behind each brand entity in the Motorhome Settings Pop-Up
 * Author(s): Jakub Patelski
 */
public class BrandEntityController implements Initializable {
    // FX Nodes
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private VBox remove;

    // This class is always going to insert the last entity inside the ArrayList.
    public final int entityIndex = Session.brandEntityList.size() - 1;

    /**
     * Function that removes a given brand entity from the database
     * @param brand object representing the entity to be deleted
     */
    private void remove(Brand brand) {
        // Get confirmation with an alert
        Alert alert = FXUtils.confirmDeletion("Brand", nameLabel.getText());
        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                // Prepare statement
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                        "DELETE FROM brands WHERE id = ?"
                );
                preparedStatement.setInt(1, brand.getId());
                preparedStatement.execute();
                // If removal is successful, use the Bridge to clear and re-fetch the brands and models to reflect changes
                Bridge.getMotorhomeSettingsController().refreshBrands();
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
        // Retrieve brand object representing the entity from ORM
        Brand brand = Session.brandEntityList.get(entityIndex);
        // Set labels to values of entity
        nameLabel.setText(brand.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(brand.getPrice()) + " €");
        // Prepare remove button functionality
        remove.setOnMouseClicked(mouseEvent -> remove(brand));
    }
}