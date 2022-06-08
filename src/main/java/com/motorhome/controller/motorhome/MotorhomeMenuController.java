package com.motorhome.controller.motorhome;

import com.motorhome.controller.main.MenuController;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the Motorhome Menu
 * Author(s): Octavian Roman
 */
public class MotorhomeMenuController extends MenuController {
    // FX nodes
    @FXML private HBox settings;
    @FXML private HBox brandToolFlipper;
    @FXML private HBox modelToolFlipper;
    @FXML private HBox typeToolFlipper;
    @FXML private HBox availabilityToolFlipper;

    @Override
    public void fetchEntities() {
        fetchEntities("brands.name", "ASC");
    }

    /**
     * Fetch the existing Motorhome Entities from the database and display them in the Menu.
     * 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
     * 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
     * 3. Retrieve Motorhome, Brand and Model entities from database and store them in ResultSet.
     * 4. Iterate over DataResult, per iteration:
     *    a) Create objects for each entry of aforementioned entities.
     *    b) Add all objects to their ORM ArrayLists in persistence.Session.
     *    f) Immediately inject a new MotorhomeEntity into the menu. This will trigger the MotorhomeEntityController, which will handle the logic.
     * 5. Set the label displaying the entity count to the amount of Motorhome objects in ORM to ensure it stays updated over multiple fetches.
     * 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
     * @param column Schema column which will be used to order the entities.
     * @param order String which will determine whether the order is ascending or descending: "ASC" or "DESC".
     */
    @Override
    public void fetchEntities(String column, String order) {
        Connection connection = Database.getConnection();
        try {
            // 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
            entityContainer.getChildren().clear();
            // 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
            Session.motorhomeEntityList.clear();
            Session.brandEntityList.clear();
            Session.modelEntityList.clear();
            // 3. Retrieve Motorhome, Brand and Model entities from database and store them in ResultSet.
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM motorhomes " +
                            "JOIN models ON motorhomes.model_id = models.id " +
                            "JOIN brands ON models.brand_id = brands.id " +
                            "ORDER BY " + column + " " + order + ";");
            ResultSet resultSet = preparedStatement.executeQuery();
            // 4. Iterate over ResultSet, per iteration:
            while (resultSet.next()) {
                // a) Create objects for each entry of aforementioned entities.
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
                // b) Add all objects to their ORM ArrayLists in persistence.Session.
                Session.motorhomeEntityList.add(motorhome);
                Session.brandEntityList.add(brand);
                Session.modelEntityList.add(model);
                // f) Immediately inject a new RentalEntity into the menu. This will trigger the MotorhomeEntityController, which will handle the logic.
                FXUtils.injectEntity("motorhome_entity", entityContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
            // 5. Set the label displaying the entity count to the amount of Rental objects in ORM to ensure it stays updated over multiple fetches.
            entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
            // 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
            currentOrder = order;
        }
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    @Override
    protected void prepareToolbar() {
        entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
        brandToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
        modelToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("models.name"));
        typeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("type"));
        availabilityToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("rented"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setMotorhomeMenuController(this);
        prepare();
        settings.setOnMouseClicked(mouseEvent -> {
            if (Session.CurrentUser.getCurrentUser().getAdmin()) {
                FXUtils.popUp("motorhome_settings", "motorhome_settings", "Motorhome Options");
                fetchEntities();
            } else {
                FXUtils.alert(Alert.AlertType.ERROR, "You do not have permission to access this menu.", "Motorhome Settings", "Unauthorized Access", true);
            }
        });

        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("motorhome_add", "popup", "Add Motorhome");
            fetchEntities();
        });
    }
}