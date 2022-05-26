package com.motorhome.controller.rental.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Extra;
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
 * Controller that handles the logic behind each extra entity in the Motorhome Settings Pop-Up
 * Author(s): Octavian Roman
 */
public class ExtraEntityController implements Initializable {
    // FX Nodes
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private VBox remove;

    // This class is always going to insert the last entity inside the ArrayList.
    public final int entityIndex = Session.extraEntityList.size() - 1;

    /**
     * Function that removes a given extra entity from the database
     * @param extra object representing the entity to be deleted
     */
    private void remove(Extra extra) {
        // Get confirmation with an alert
        Alert alert = FXUtils.confirmDeletion("Extra", nameLabel.getText());
        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                // Prepare statement
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                        "DELETE FROM extras WHERE id = ?"
                );
                preparedStatement.setInt(1, extra.getId());
                preparedStatement.execute();
                // If removal is successful, use the Bridge to clear and re-fetch the extras to reflect changes
                Bridge.getRentalSettingsController().fetchExtras();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.closeConnection(connection);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Extra extra = Session.extraEntityList.get(entityIndex);
        nameLabel.setText(extra.getName());
        priceLabel.setText(FXUtils.formatCurrencyValues(extra.getPrice()) + " €");
        remove.setOnMouseClicked(mouseEvent -> remove(extra));
    }
}