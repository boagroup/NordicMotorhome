package com.motorhome.controller.rental;

import com.motorhome.FXUtils;
import com.motorhome.model.Extra;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalExtraSelectionController implements Initializable {

    @FXML private VBox extrasContainer;

    /**
     * Retrieves extras from the database iteratively and injects them into the container where they belong
     */
    private void fetchExtras() {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            Session.extraEntityList.clear();
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
                FXUtils.injectEntity("extra_selection_entity", extrasContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchExtras();
    }
}