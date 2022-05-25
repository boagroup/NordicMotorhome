package com.motorhome.controller.rental;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.*;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox motorhomeToolFlipper;
    @FXML private HBox clientToolFlipper;
    @FXML private HBox startDateToolFlipper;
    @FXML private HBox endDateToolFlipper;
    @FXML private HBox priceToolFlipper;
    @FXML private HBox settings;
    @FXML private HBox add;

        private String currentOrder;

        private void fetchRentals(String column, String order) {
            Connection connection = Database.getConnection();
            try {
                Session.rentalEntityList.clear();
                Session.clientEntityList.clear();
                Session.motorhomeEntityList.clear();
                Session.modelEntityList.clear();
                Session.brandEntityList.clear();
                Session.rentalExtrasCollectionList.clear();
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                "SELECT * FROM rentals " +
                    "JOIN clients ON rentals.id = clients.rental_id " +
                    "JOIN motorhomes ON rentals.motorhome_id = motorhomes.id " +
                    "JOIN models ON motorhomes.model_id = models.id " +
                    "JOIN brands ON models.brand_id = brands.id " +
                    "ORDER BY " + column + " " + order + ";");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
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

                    preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                            "SELECT * FROM rentalextras " +
                                "JOIN extras ON extra_id = extras.id " +
                                "WHERE rental_id = ?");

                    preparedStatement.setInt(1, resultSet.getInt("rentals.id"));
                    ResultSet extraResultSet = preparedStatement.executeQuery();
                    ArrayList<Extra> extraArrayList = new ArrayList<>();
                    while (extraResultSet.next()) {
                        Extra extra = new Extra(
                                extraResultSet.getInt("id"),
                                extraResultSet.getString("name"),
                                extraResultSet.getDouble("price")
                        );
                        extraArrayList.add(extra);
                    }
                    // Add objects as soon as they are created, ensuring correct order
                    Session.rentalEntityList.add(rental);
                    Session.clientEntityList.add(client);
                    Session.motorhomeEntityList.add(motorhome);
                    Session.modelEntityList.add(model);
                    Session.brandEntityList.add(brand);
                    Session.rentalExtrasCollectionList.add(extraArrayList);
                    // Inject what we just inserted into the ArrayLists into the scene
                    FXUtils.injectEntity("rental_entity", entityContainer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.closeConnection(connection);
                // Store what order we just fetched with to be able to flip it later, if needed
                currentOrder = order;
            }
        }

        /**
         * Flips the order of the entities in the Scene.
         * @param field Field that is selected to flip the order (e.g. "start date")
         */

        private void flipOrder(String field) {
            entityContainer.getChildren().clear();
            if (currentOrder.equals("ASC")) {
                fetchRentals(field, "DESC");
            } else fetchRentals(field, "ASC");
        }

        /**
         * Load flipping functions into toolbar at the top of the entity container to allow order inversion
         */
        private void prepareStaffToolbar() {
            entityCountLabel.setText(Session.rentalEntityList.size() + " Items");
            motorhomeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
            clientToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("firstName"));
            startDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("start_date"));
            endDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("end_date"));
            priceToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("final_price"));
        }

        /**
         * Inserts add functionality onto the "add" button.
         * Logic is handled by an external view.
         */
        private void addFunctionality() {
            add.setOnMouseClicked(mouseEvent -> {
                FXUtils.popUp("rental_add", "popup", "Add Rentals");
                refresh();
            });
        }

        /**
         * Method that clears the entity container and fills it again.
         * Used to refresh the entities.
         * Public to use it from other controllers with Bridge.
         */
        public void refresh() {
            entityContainer.getChildren().clear();
            fetchRentals("firstName", "ASC");
            entityCountLabel.setText(Session.rentalEntityList.size() + " Items");
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            Bridge.setRentalMenuController(this);

            FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

            settings.setOnMouseClicked(mouseEvent -> FXUtils.popUp("rental_settings", "motorhome_settings", "Rental Options"));

            addFunctionality();

            fetchRentals("firstName", "ASC");

            prepareStaffToolbar();

            backButton.setOnAction(actionEvent -> FXUtils.changeRoot("main_menu","main_menu", backButton));
        }
    }