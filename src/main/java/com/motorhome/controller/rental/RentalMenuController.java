package com.motorhome.controller.rental;

import com.motorhome.FXUtils;
import com.motorhome.model.Rental;
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


    // Can be used to flip order, not using boolean for clarity.
        private String currentOrder;

        private void fetchRentals(String column, String order) {
            Connection connection = Database.getConnection();
            try {
                // Clear arraylists to be safe
                Session.rentalEntityList.clear();
                // Select everything from both tables, long statement because we need to decrypt the password
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                        "");

                ResultSet resultSet = preparedStatement.executeQuery();
                // Iterate over each entity in the result set, and create Rental objects with each one of them
                while (resultSet.next()) {
                    Rental rental = new Rental(
                            resultSet.getInt("rentals.id"),
                            resultSet.getInt("motorhome_id"),
                            resultSet.getString("state"),
                            resultSet.getInt("distance"),
                            resultSet.getString("season"),
                            resultSet.getDate("start_date"),
                            resultSet.getDate("end_date"),
                            resultSet.getString("notes")
                    );

                    // Add objects as soon as they are created, ensuring correct order
                    Session.rentalEntityList.add(rental);
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
         *Flips the order of the entities in the Scene.
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
            motorhomeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("motorhome"));
            clientToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("client"));
            startDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("start_date"));
            endDateToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("end_date"));
            priceToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("price"));
        }

        /**
         * Inserts add functionality onto the "add" button.
         * Logic is handled by an external view.
         */
        private void addFunctionality() {
            add.setOnMouseClicked(mouseEvent -> {
                FXUtils.popUp("rental_add", "Add Rentals");
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

            settings.setOnMouseClicked(mouseEvent -> {
                FXUtils.popUp("rental_settings", "motorhome_settings", "Rental Options");
            });

            addFunctionality();

            backButton.setOnAction(actionEvent -> FXUtils.changeRoot("main_menu","main_menu", backButton));
        }
    }
