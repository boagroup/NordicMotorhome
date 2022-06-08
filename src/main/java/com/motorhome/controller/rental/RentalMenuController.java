package com.motorhome.controller.rental;

import com.motorhome.controller.main.MenuController;
import com.motorhome.model.*;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller that handles the logic behind the Rental Menu.
 * Author(s): Octavian Roman
 */
public class RentalMenuController extends MenuController {
    // FX nodes
    @FXML private HBox motorhomeToolFlipper;
    @FXML private HBox clientToolFlipper;
    @FXML private HBox startDateToolFlipper;
    @FXML private HBox endDateToolFlipper;
    @FXML private HBox priceToolFlipper;
    @FXML private HBox settings;

    @Override
    public void fetchEntities() {
        fetchEntities("firstName", "ASC");
    }
    
    /**
     * Fetch the existing Rental Entities from the database and display them in the Menu.
     * @param column Schema column which will be used to order the entities.
     * @param order String which will determine whether the order is ascending or descending: "ASC" or "DESC".
     */
    @Override
    public void fetchEntities(String column, String order) {
    /*
     * 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
     * 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
     * 3. Retrieve Rental, Client, Motorhome, Model and Brand entities from database and store them in ResultSet.
     * 4. Iterate over ResultSet, per iteration:
     *    a) Create objects for each entry of aforementioned entities.
     *    b) Retrieve via ID from database all Extra entities associated with the Rental object, store them all in extraResultSet.
     *    c) Instantiate an ArrayList of Extra objects.
     *    d) Iterate over extraResultSet, and for each entry, create Extra objects which are then added to the ArrayList.
     *    e) Add all objects and the Extra ArrayList to their ORM ArrayLists in persistence.Session.
     *    f) Immediately inject a new RentalEntity into the menu. This will trigger the RentalEntityController, which will handle the logic.
     * 5. Set the label displaying the entity count to the amount of Rental objects in ORM to ensure it stays updated over multiple fetches.
     * 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
     */
        Connection connection = Database.getConnection();
        try {
            // 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
            entityContainer.getChildren().clear();
            // 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
            Session.rentalEntityList.clear();
            Session.clientEntityList.clear();
            Session.motorhomeEntityList.clear();
            Session.modelEntityList.clear();
            Session.brandEntityList.clear();
            Session.rentalExtrasCollectionList.clear();
            // 3. Retrieve Rental, Client, Motorhome, Model and Brand entities from database and store them in ResultSet.
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM rentals " +
                            "JOIN clients ON rentals.id = clients.rental_id " +
                            "JOIN motorhomes ON rentals.motorhome_id = motorhomes.id " +
                            "JOIN models ON motorhomes.model_id = models.id " +
                            "JOIN brands ON models.brand_id = brands.id " +
                            "ORDER BY " + column + " " + order + ";");
            ResultSet resultSet = preparedStatement.executeQuery();
            // 4. Iterate over ResultSet, per iteration:
            while (resultSet.next()) {
                // a) Create objects for each entry of aforementioned entities.
                Rental rental = new Rental(
                        resultSet.getInt("rentals.id"),
                        resultSet.getInt("motorhome_id"),
                        resultSet.getInt("distance"),
                        resultSet.getString("location"),
                        resultSet.getString("season"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("final_price"),
                        resultSet.getString("notes")
                );
                Client client = new Client(
                        resultSet.getInt("clients.id"),
                        resultSet.getInt("rentals.id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("telephone")
                );
                Motorhome motorhome = new Motorhome(
                        resultSet.getInt("motorhomes.id"),
                        resultSet.getInt("model_id"),
                        resultSet.getString("image"),
                        resultSet.getBoolean("rented"),
                        resultSet.getString("type"),
                        resultSet.getInt("beds")
                );
                Model model = new Model(
                        resultSet.getInt("models.id"),
                        resultSet.getInt("brand_id"),
                        resultSet.getString("models.name"),
                        resultSet.getDouble("models.price")
                );
                Brand brand = new Brand(
                        resultSet.getInt("brands.id"),
                        resultSet.getString("brands.name"),
                        resultSet.getDouble("brands.price")
                );
                // b) Retrieve via ID from database all Extra entities associated with the Rental object, store them all in extraResultSet.
                preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                        "SELECT * FROM rentalextras " +
                                "JOIN extras ON extra_id = extras.id " +
                                "WHERE rental_id = ?");
                preparedStatement.setInt(1, resultSet.getInt("rentals.id"));
                ResultSet extraResultSet = preparedStatement.executeQuery();
                // c) Instantiate an ArrayList of Extra objects.
                ArrayList<Extra> extraArrayList = new ArrayList<>();
                // d) Iterate over extraResultSet, and for each entry, create Extra objects which are then added to the ArrayList.
                while (extraResultSet.next()) {
                    Extra extra = new Extra(
                            extraResultSet.getInt("id"),
                            extraResultSet.getString("name"),
                            extraResultSet.getDouble("price")
                    );
                    extraArrayList.add(extra);
                }
                // e) Add all objects and the Extra ArrayList to their ORM ArrayLists in persistence.Session.
                Session.rentalEntityList.add(rental);
                Session.clientEntityList.add(client);
                Session.motorhomeEntityList.add(motorhome);
                Session.modelEntityList.add(model);
                Session.brandEntityList.add(brand);
                Session.rentalExtrasCollectionList.add(extraArrayList);
                // f) Immediately inject a new RentalEntity into the menu. This will trigger the RentalEntityController, which will handle the logic
                FXUtils.injectEntity("rental_entity", entityContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
            // 5. Set the label displaying the entity count to the amount of Rental objects in ORM to ensure it stays updated over multiple fetches.
            // Needed because this gets called after adding or deleting, which means that the count may have changed since the last time it was called.
            entityCountLabel.setText(Session.rentalEntityList.size() + " Items");
            // 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
            currentOrder = order;
        }
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    @Override
    protected void prepareToolbar() {
        motorhomeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
        clientToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("firstName"));
        startDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("start_date"));
        endDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("end_date"));
        priceToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("final_price"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalMenuController(this);
        prepare();


        settings.setOnMouseClicked(mouseEvent -> {
            if (Session.CurrentUser.getCurrentUser().getAdmin()) {
                FXUtils.popUp("rental_settings", "motorhome_settings", "Rental Settings");
                fetchEntities();
            } else {
                FXUtils.alert(Alert.AlertType.ERROR, "You do not have permission to access this menu.", "Rental Settings", "Unauthorized Access", true);
            }
        });
        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("rental_add", "popup", "Add Rentals");
            fetchEntities();
        });
    }
}