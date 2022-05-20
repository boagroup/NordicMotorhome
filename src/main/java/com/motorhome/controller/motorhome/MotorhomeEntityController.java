package com.motorhome.controller.motorhome;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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

    private void deleteMotorhome(Motorhome motorhome) {
        // Get confirmation with an alert. No function for this since they'd have to take too many arguments anyways.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete "+ brandLabel.getText() + modelLabel.getText() + "?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Please Confirm Motorhome Deletion");
        alert.setTitle("Motorhome Deletion");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/assets/alt_icon.png")).toExternalForm()));
        ImageView image = new ImageView();
        image.setImage(image.getImage());
        // Must explicitly size image, otherwise it preserves original size and makes pop-up grow
        image.setFitHeight(64.0);
        image.setFitWidth(64.0);
        alert.setGraphic(image);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Connection connection = SimpleDatabase.getConnection();
            try {
                // Execute SQL statement.
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement("DELETE FROM motorhomes WHERE id = ?");
                preparedStatement.setInt(1, motorhome.getId());
                preparedStatement.execute();

                // Refresh the menu
                Bridge.getMotorhomeMenuController().refresh();

                // Show success alert.
                FXUtils.alert(Alert.AlertType.INFORMATION, "The motorhome has been deleted",
                        "Motorhome Deleted", "Deletion Successful", true);
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.alert(Alert.AlertType.ERROR, "Error", "Deletion Error", "Something went wrong! (SQL Error)", false);
            } finally {
                SimpleDatabase.closeConnection(connection);
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

        delete.setOnAction(actionEvent -> deleteMotorhome(motorhome));
        edit.setOnAction(actionEvent -> {
            MotorhomeEditController.entityIndex = entityIndex;
            FXUtils.popUp("motorhome_edit", "popup", "Edit Motorhome");
            Bridge.getMotorhomeMenuController().refresh();
        });
    }
}