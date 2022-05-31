package com.motorhome.controller.rental.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.controller.rental.popup.RentalEditController;
import com.motorhome.model.*;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the pop-up that facilitates motorhome selection.
 * Author(s): Octavian Roman
 */
public class RentalEntityController implements Initializable {
    // FX Nodes
    @FXML private ImageView image;
    @FXML private Label motorhomeLabel;
    @FXML private Label clientLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label priceLabel;
    @FXML private MenuItem edit;
    @FXML private MenuItem delete;

    // This class is always going to insert the last entity inside the ArrayList.
    // The same index for brands and models since they get added simultaneously.
    public final int entityIndex = Session.rentalEntityList.size() - 1;

    /**
     * Remove a Rental entity from the Database.
     * Also sets the status of a Motorhome to "available"
     * @param rental Rental object representing the entity to be removed.
     * @param motorhome Motorhome object associated with the rental
     * @param client Client object associated with the rental. Deletion will cascade to it.
     */
    private void remove(Rental rental, Motorhome motorhome, Client client) {
        Alert alert = FXUtils.confirmDeletion("Rental", client.getFirstName(), client.getLastName(), image);
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                        "DELETE FROM rentals WHERE id = ?;");
                preparedStatement.setInt(1, rental.getId());
                preparedStatement.execute();

                preparedStatement = connection.prepareStatement(
                        "UPDATE motorhomes SET rented = 0 WHERE id = ?;");
                preparedStatement.setInt(1, motorhome.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.closeConnection(connection);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rental rental = Session.rentalEntityList.get(entityIndex);
        Client client = Session.clientEntityList.get(entityIndex);
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);

        Image i = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        image.setImage(i);
        motorhomeLabel.setText(brand.getName() + " " + model.getName());
        clientLabel.setText(client.getFirstName() + " " + client.getLastName());
        startDateLabel.setText(String.valueOf(rental.getStart_date()));
        endDateLabel.setText(String.valueOf(rental.getEnd_date()));
        priceLabel.setText(FXUtils.formatCurrencyValues(rental.getFinal_price()) + " €");

        edit.setOnAction(actionEvent -> {
            RentalEditController.entityIndex = entityIndex;
            FXUtils.popUp("rental_edit", "popup", "Edit Rental");
            Bridge.getRentalMenuController().fetchEntities("firstName", "ASC");
        });

        delete.setOnAction(actionEvent -> {
            remove(rental, motorhome, client);
            Bridge.getRentalMenuController().fetchEntities("firstName", "ASC");
        });
    }
}
