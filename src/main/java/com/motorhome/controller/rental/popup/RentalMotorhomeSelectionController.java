package com.motorhome.controller.rental.popup;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalMotorhomeSelectionController implements Initializable {

    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox brandToolFlipper;
    @FXML private HBox modelToolFlipper;
    @FXML private HBox typeToolFlipper;
    @FXML private HBox availabilityToolFlipper;

    private String currentOrder;

    private void fetchMotorhomes(String column, String order) {
        Connection connection = Database.getConnection();
        try {
            Session.motorhomeEntityList.clear();
            Session.brandEntityList.clear();
            Session.modelEntityList.clear();
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM motorhomes " +
                        "JOIN models ON motorhomes.model_id = models.id " +
                        "JOIN brands ON models.brand_id = brands.id " +
                        "WHERE motorhomes.rented = 0 " +
                        "ORDER BY " + column + " " + order + ";");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Motorhome motorhome = new Motorhome(
                        resultSet.getInt("motorhomes.id"),
                        resultSet.getInt("model_id"),
                        resultSet.getString("image"),
                        resultSet.getBoolean("rented"),
                        resultSet.getString("type"),
                        resultSet.getInt("beds")
                );
                Brand brand = new Brand(
                        resultSet.getInt("brands.id"),
                        resultSet.getString("brands.name"),
                        resultSet.getDouble("brands.price")
                );
                Model model = new Model(
                        resultSet.getInt("models.id"),
                        resultSet.getInt("brand_id"),
                        resultSet.getString("models.name"),
                        resultSet.getDouble("models.price")
                );
                Session.motorhomeEntityList.add(motorhome);
                Session.brandEntityList.add(brand);
                Session.modelEntityList.add(model);
                FXUtils.injectEntity("motorhome_selection_entity", entityContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
            currentOrder = order;
        }
    }

    /**
     * Flips the order of the entities in the Scene.
     * @param field Field that is selected to flip the order (e.g. "brands.name")
     */
    private void flipOrder(String field) {
        entityContainer.getChildren().clear();
        if (currentOrder.equals("ASC")) {
            fetchMotorhomes(field, "DESC");
        } else fetchMotorhomes(field, "ASC");
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    private void prepareToolbar() {
        entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
        brandToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
        modelToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("models.name"));
        typeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("type"));
        availabilityToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("rented"));
    }

    public void closeSelectionWindow() {
        Stage stage = (Stage) entityContainer.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalMotorhomeSelectionController(this);
        fetchMotorhomes("brands.name", "ASC");
        prepareToolbar();
    }
}