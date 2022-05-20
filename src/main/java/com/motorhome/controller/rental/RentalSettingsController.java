package com.motorhome.controller.rental;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Extra;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller that handles the logic behind the Rental Settings Pop-Up
 * Author(s): Octavian Roman
 */
public class RentalSettingsController implements Initializable {

    @FXML private TextField extraNameField;
    @FXML private TextField extraPriceField;
    @FXML private HBox addExtraButton;
    @FXML private VBox extrasContainer;

    /**
     * Retrieves extras from the database iteratively and injects them into the container where they belong
     */
    private void fetchExtras() {
        // Establish connection
        Connection connection = SimpleDatabase.getConnection();
        try {
            // Prepare SQL statement
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM extras ORDER BY name;");
            // Execute statement and store result in a ResultSet
            ResultSet resultSet = preparedStatement.executeQuery();
            // While there are brands in the resultSet
            Session.extraEntityList.clear();
            while (resultSet.next()) {
                // Create extra object for each entity
                Extra extra = new Extra(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price")
                );
                // Add extra object to ArrayList for further manipulation
                Session.extraEntityList.add(extra);
                // Finally, inject the entity view into the container, triggering its controller
                FXUtils.injectEntity("extra_entity", extrasContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleDatabase.closeConnection(connection);
        }
    }

    public void refreshExtras() {
        extrasContainer.getChildren().clear();
        fetchExtras();
    }

    /**
     * Adds a new extra to the database in accordance to the values inserted in the fields at the top of the tab
     */
    private void addExtra() {
        // Establish connection
        Connection connection = SimpleDatabase.getConnection();
        try {
            // Prepare and execute SQL statement
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO extras (name, price) VALUES (?,?);"
            );
            preparedStatement.setString(1, extraNameField.getText().equals("") ? null : extraNameField.getText());
            preparedStatement.setDouble(2, Double.parseDouble(extraPriceField.getText()));
            preparedStatement.execute();
            // Clear and re-fetch the brands to reflect changes
            refreshExtras();
            // Clear fields from inserted values
            extraNameField.setText("");
            extraPriceField.setText("");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            // Print error message if something goes wrong
            FXUtils.alert(
                    Alert.AlertType.ERROR,
                    "Invalid name or price\nMake sure no field is blank and name is not repeated",
                    "Error",
                    "SQL Error",
                    true);
        } finally {
            SimpleDatabase.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalSettingsController(this);
        fetchExtras();
        addExtraButton.setOnMouseClicked(mouseEvent -> addExtra());
        FXUtils.formatCurrencyFields(extraPriceField);
    }
}
