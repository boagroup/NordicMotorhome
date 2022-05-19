package com.motorhome.controller.motorhome;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller that handles the logic behind each model entity in the Motorhome Settings Pop-Up
 * Author(s): Octavian Roman
 */
public class ModelEntityController implements Initializable {

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete "+ nameLabel.getText() + "?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Please Confirm Model Deletion");
        alert.setTitle("Model Deletion");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/assets/alt_icon.png")).toExternalForm()));
        alert.showAndWait();

        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = SimpleDatabase.getConnection();
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
                SimpleDatabase.closeConnection(connection);
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
