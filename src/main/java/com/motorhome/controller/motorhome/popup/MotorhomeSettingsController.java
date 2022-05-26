package com.motorhome.controller.motorhome.popup;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
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
 * Controller that handles the logic behind the Motorhome Settings Pop-Up
 * Author(s): Octavian Roman
 */
public class MotorhomeSettingsController implements Initializable {
    // FX Nodes
    // Brands Tab
    @FXML private TextField brandNameField;
    @FXML private TextField brandPriceField;
    @FXML private HBox addBrandButton;
    @FXML private VBox brandsContainer;
    // Models Tab
    @FXML private TextField modelNameField;
    @FXML private TextField modelPriceField;
    @FXML private ChoiceBox<String> brandChoiceBox;
    @FXML private HBox addModelButton;
    @FXML private VBox modelsContainer;

    /**
     * Adds options to Brand ChoiceBox.
     */
    private void setBrandOptions(Connection connection) {
        brandChoiceBox.getItems().clear();
        brandChoiceBox.setValue("Brand");
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT name FROM brands;");
            ResultSet resultSet = preparedStatement.executeQuery();
            brandChoiceBox.getItems().clear();
            while (resultSet.next()) {
                brandChoiceBox.getItems().add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves models from the database iteratively and injects them into the container where they belong
     */
    public void fetchModels() {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            // Prepare SQL statement
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM models JOIN brands ON models.brand_id = brands.id ORDER BY models.name;");
            // Execute statement and store result in a ResultSet
            ResultSet resultSet = preparedStatement.executeQuery();
            // Clear arraylists to ensure data integrity
            Session.modelEntityList.clear();
            Session.brandEntityList.clear();
            // While there are models in the resultSet...
            while (resultSet.next()) {
                // Create model object for each entity
                Model model = new Model(
                        resultSet.getInt("models.id"),
                        resultSet.getInt("brand_id"),
                        resultSet.getString("models.name"),
                        resultSet.getDouble("models.price")
                );
                // Create brand object for each entity
                Brand brand = new Brand(
                        resultSet.getInt("brands.id"),
                        resultSet.getString("brands.name"),
                        resultSet.getDouble("brands.price")
                );
                // Add brand and model object to ArrayLists for further manipulation
                Session.modelEntityList.add(model);
                Session.brandEntityList.add(brand);
                // Finally, inject the entity view into the container, triggering its controller
                FXUtils.injectEntity("model_entity", modelsContainer);
            }
            // Update the brand options for new models every time we fetch
            setBrandOptions(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
    }

    // Remove all models from their container
    public void refreshModels() {
        modelsContainer.getChildren().clear();
        fetchModels();
    }

    /**
     * Adds a new model to the database in accordance to the values inserted in the fields at the top of the tab
     */
    private void addModel() {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            // First, we need to get the ID of the brand to which this model belongs
            int brandId;
            // Prepare statement to do the aforementioned with the brand name
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT id FROM brands WHERE name = ?;"
            );
            // Set name from choice box
            preparedStatement.setString(1, brandChoiceBox.getValue());
            // Execute and store result in brandId variable
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            brandId = resultSet.getInt("id");

            // Now we can add the actual model. Prepare and execute statement
            preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO models (name, brand_id, price) VALUES (?,?,?);"
            );
            preparedStatement.setString(1, modelNameField.getText().equals("") ? null : modelNameField.getText());
            preparedStatement.setInt(2, brandId);
            preparedStatement.setDouble(3, Double.parseDouble(modelPriceField.getText()));
            preparedStatement.execute();
            // Clear and re-fetch the brands to reflect changes
            refreshModels();
            // Clear fields from inserted values
            modelNameField.setText("");
            modelPriceField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            // Print error message if something goes wrong
            FXUtils.alert(
                    Alert.AlertType.ERROR,
                    "Invalid name, price or brand\nMake sure no field is blank and name is not repeated",
                    "Error",
                    "SQL Error",
                    true);
        } finally {
            Database.closeConnection(connection);
        }
    }

    /**
     * Retrieves brands from the database iteratively and injects them into the container where they belong
     */
    public void fetchBrands() {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            // Prepare SQL statement
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM brands ORDER BY name;");
            // Execute statement and store result in a ResultSet
            ResultSet resultSet = preparedStatement.executeQuery();
            // While there are brands in the resultSet
            Session.brandEntityList.clear();
            while (resultSet.next()) {
                // Create brand object for each entity
                Brand brand = new Brand(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price")
                );
                // Add brand object to ArrayList for further manipulation
                Session.brandEntityList.add(brand);
                // Finally, inject the entity view into the container, triggering its controller
                FXUtils.injectEntity("brand_entity", brandsContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
    }

    // Remove all brands from the container
    public void refreshBrands() {
        brandsContainer.getChildren().clear();
        fetchBrands();
    }

    /**
     * Adds a new brand to the database in accordance to the values inserted in the fields at the top of the tab
     */
    private void addBrand() {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            // Prepare and execute SQL statement
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO brands (name, price) VALUES (?,?);"
            );
            preparedStatement.setString(1, brandNameField.getText().equals("") ? null : brandNameField.getText());
            preparedStatement.setDouble(2, Double.parseDouble(brandPriceField.getText()));
            preparedStatement.execute();
            // Clear and re-fetch the brands to reflect changes
            refreshBrands();
            // Clear fields from inserted values
            brandNameField.setText("");
            brandPriceField.setText("");
            setBrandOptions(connection);
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
            Database.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setMotorhomeSettingsController(this);
        fetchModels();
        fetchBrands();
        addModelButton.setOnMouseClicked(mouseEvent -> addModel());
        addBrandButton.setOnMouseClicked(mouseEvent -> addBrand());
        FXUtils.formatCurrencyFields(brandPriceField);
        FXUtils.formatCurrencyFields(modelPriceField);
    }
}
