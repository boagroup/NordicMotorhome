package com.motorhome.controller.motorhome.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.controller.motorhome.popup.MotorhomeEditController;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    /**
     * Use an object to remove a motorhome entity from the database.
     * @param motorhome Motorhome object representing the motorhome entity in the database.
     */
    private void remove(Motorhome motorhome) {
        Alert alert = FXUtils.confirmDeletion("Motorhome", brandLabel.getText(), modelLabel.getText(), image);
        // If alert is confirmed
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                // Execute SQL statement
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement("DELETE FROM motorhomes WHERE id = ?");
                preparedStatement.setInt(1, motorhome.getId());
                preparedStatement.execute();

                // Refresh the menu
                Bridge.getMotorhomeMenuController().fetchEntities("brands.name", "ASC");

                // Show success alert
                FXUtils.alert(Alert.AlertType.INFORMATION, "The motorhome has been deleted",
                        "Motorhome Deleted", "Deletion Successful", true);
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.alert(Alert.AlertType.ERROR, "Error", "Deletion Error", "Something went wrong! (SQL Error)", false);
            } finally {
                Database.closeConnection(connection);
            }
        }
    }

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

        delete.setOnAction(actionEvent -> remove(motorhome));
        edit.setOnAction(actionEvent -> {
            MotorhomeEditController.entityIndex = entityIndex;
            FXUtils.popUp("motorhome_edit", "popup", "Edit Motorhome");
            Bridge.getMotorhomeMenuController().fetchEntities("brands.name", "ASC");
        });
    }
}