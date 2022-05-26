package com.motorhome.controller.rental.popup;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.controller.rental.entity.ExtraSelectionEntityController;
import com.motorhome.model.*;
import com.motorhome.persistence.Database;
import com.motorhome.persistence.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the Rental Add Pop-Up
 * Author(s): Octavian Roman
 */
public class RentalEditController implements Initializable {
    // FX Nodes
    @FXML private Label title;
    @FXML private TextField clientFirstNameField;
    @FXML private TextField clientLastNameField;
    @FXML private TextField clientTelephoneField;
    @FXML private ChoiceBox<String> seasonChoiceBox;
    @FXML private TextField pickUpLocationField;
    @FXML private TextField distanceField;
    @FXML private DatePicker startDateDatePicker;
    @FXML private DatePicker endDateDatePicker;
    @FXML private ImageView image;
    @FXML private TextArea notes;
    @FXML private Label dailyPriceLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button addExtrasButton;
    @FXML private Button confirmButton;

    // Index gets changed as the user selects "Edit" on a different motorhome
    public static int entityIndex = -1;
    // integer gets changed via Bridge as the user picks different motorhomes in their pop-up
    public int motorhomeId = 0;

    // FX String properties that can be modified dynamically as the user modifies the rental characteristics
    private final StringProperty dynamicTitle = new SimpleStringProperty();
    private final StringProperty dynamicDailyPrice = new SimpleStringProperty();
    private final StringProperty dynamicTotalPrice = new SimpleStringProperty();

    /**
     * Retrieve motorhome data for a given entity and load it into the fields.
     * Dynamic labels (title + price) also need to be set initially, hence the SQL statements
     * @param motorhome Motorhome object representing the entity whose data is to be loaded into the fields
     */
    private void loadDataIntoFields(Rental rental, Client client, Motorhome motorhome, Model model, Brand brand, ArrayList<Extra> extraArrayList) {
        clientFirstNameField.setText(client.getFirstName());
        clientLastNameField.setText(client.getLastName());
        clientTelephoneField.setText(client.getTelephone());
        String season = "Season";
        switch (rental.getSeason()) {
            case "L" -> season = "Low Season";
            case "M" -> season = "Mid Season";
            case "P" -> season = "Peak Season";
        }
        seasonChoiceBox.setValue(season);
        pickUpLocationField.setText(rental.getLocation());
        distanceField.setText(String.valueOf(rental.getDistance()));
        startDateDatePicker.setValue(rental.getStart_date().toLocalDate());
        endDateDatePicker.setValue(rental.getEnd_date().toLocalDate());
        // Retrieve image and create new FX image node with it
        Image image = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        // Put node into ImageView
        this.image.setImage(image);
        notes.setText(rental.getNotes());
        Session.extraSelectionList.clear();
        Session.extraSelectionList.addAll(extraArrayList);
        motorhomeId = motorhome.getId();
        updateDynamicFields(model, brand);
    }

    /**
     * Performs the appropriate calculations to update dynamic fields (title, prices) when called.
     * @param model Model object to compute price and update title (if new motorhome is picked)
     * @param brand Brand entity to compute price and update title (if new motorhome is picked)
     */
    private void updateDynamicFields(Model model, Brand brand) {
        // Set dynamic title value to brand name + model name
        dynamicTitle.setValue(brand.getName() + " " + model.getName());
        // Compute daily price (brand and model needed)
        double dailyPrice = FXUtils.computeDailyPrice(brand, model, seasonChoiceBox);
        // Set dynamic daily price to computer price and format accordingly
        dynamicDailyPrice.setValue(FXUtils.formatCurrencyValues(dailyPrice) + " €");
        // If both date pickers have a value
        if (startDateDatePicker.getValue() != null && endDateDatePicker.getValue() != null) {
            // Set dynamic total price to computer price with overloaded function
            dynamicTotalPrice.setValue(
                    FXUtils.formatCurrencyValues(FXUtils.computeFinalPrice(
                            dailyPrice,
                            Date.valueOf(startDateDatePicker.getValue()),
                            Date.valueOf(endDateDatePicker.getValue()),
                            Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                            Session.extraSelectionList)) + " €");
        } else {  // Same, but if no dates have been picked
            dynamicTotalPrice.setValue(
                    FXUtils.formatCurrencyValues(FXUtils.computeFinalPrice(
                            dailyPrice,
                            Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                            Session.extraSelectionList)) + " €");
        }
    }

    /**
     * Retrieves motorhome data and updates the dynamic fields if a motorhome has been selected
     * Public to call it from the Bridge when checking/unchecking extras in the "Pick Extras" pop-up
     */
    public void reflectFieldChanges() {
        if (motorhomeId != 0) {
            Object[] entityArray = FXUtils.retrieveMotorhomeEntities(motorhomeId);
            Model model = (Model) Objects.requireNonNull(entityArray)[1];
            Brand brand = (Brand) Objects.requireNonNull(entityArray)[2];
            updateDynamicFields(model, brand);
        }
    }

    /**
     * Bind label nodes to StringProperties and make them update reactively.
     * Unidirectional data flow.
     */
    private void addReactivity() {
        // Bind labels to dynamic variables
        title.textProperty().bind(dynamicTitle);
        dailyPriceLabel.textProperty().bind(dynamicDailyPrice);
        totalPriceLabel.textProperty().bind(dynamicTotalPrice);
        // Set actions which will trigger new calculations and UI re-render
        seasonChoiceBox.setOnAction(actionEvent -> reflectFieldChanges());
        distanceField.setOnKeyTyped(actionEvent -> reflectFieldChanges());
        startDateDatePicker.setOnAction(actionEvent -> reflectFieldChanges());
        endDateDatePicker.setOnAction(actionEvent -> reflectFieldChanges());
    }

    /**
     * Take a Rental and Client object and update their values to match the fields of the pop-up.
     * @param rental Rental object representing the rental entity we are about to update with editRental().
     * @param client Client object representing the rental entity we are about to update with editRental().
     * @return An array with the Rental object at index 0 and Client object and index 1
     */
    private Object[] updateEntities(Rental rental, Client client) {
        String seasonEnum =  "";
        switch (seasonChoiceBox.getValue()) {
            case "Low Season" -> seasonEnum = "L";
            case "Mid Season" -> seasonEnum = "M";
            case "Peak Season" -> seasonEnum = "P";
        }
        Object[] entityArray = FXUtils.retrieveMotorhomeEntities(motorhomeId);
        Model model = (Model) Objects.requireNonNull(entityArray)[1];
        Brand brand = (Brand) Objects.requireNonNull(entityArray)[2];
        double dailyPrice = FXUtils.computeDailyPrice(brand, model, seasonChoiceBox);
        double finalPrice = FXUtils.computeFinalPrice(
                dailyPrice,
                java.sql.Date.valueOf(startDateDatePicker.getValue()),
                java.sql.Date.valueOf(endDateDatePicker.getValue()),
                Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                Session.extraSelectionList);

        rental.setDistance(Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()));
        rental.setLocation(pickUpLocationField.getText());
        rental.setSeason(seasonEnum);
        rental.setStart_date(java.sql.Date.valueOf(startDateDatePicker.getValue()));
        rental.setEnd_date(java.sql.Date.valueOf(endDateDatePicker.getValue()));
        rental.setFinal_price(finalPrice);
        rental.setNotes(notes.getText());

        client.setFirstName(clientFirstNameField.getText().equals("") ? null : clientFirstNameField.getText());
        client.setLastName(clientLastNameField.getText());
        client.setTelephone(clientTelephoneField.getText());

        Object[] result = new Object[2];
        result[0] = rental;
        result[1] = client;
        return result;
    }

    /**
     * Takes a rental and client object and updates the database entries with their attribute values.
     * @param r Rental object where the new values are stored
     * @param c Client object where the new values are stored
     * @return true if successful, false otherwise
     */
    private boolean editRental(Rental r, Client c) {
        Object[] entityArray = updateEntities(r, c);
        Rental rental = (Rental) entityArray[0];
        Client client = (Client) entityArray[1];
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "UPDATE rentals SET distance = ?, location = ?, season = ?, start_date = ?, end_date = ?, final_price = ?, notes = ? " +
                        "WHERE id = ?");
            preparedStatement.setInt(1, rental.getDistance());
            preparedStatement.setString(2, rental.getLocation());
            preparedStatement.setString(3, rental.getSeason());
            preparedStatement.setDate(4, rental.getStart_date());
            preparedStatement.setDate(5, rental.getEnd_date());
            preparedStatement.setDouble(6, rental.getFinal_price());
            preparedStatement.setString(7, rental.getNotes());
            preparedStatement.setInt(8, rental.getId());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(
                    "UPDATE clients SET firstName = ?, lastName = ?, telephone = ? " +
                        "WHERE id = ?");
            preparedStatement.setString(1, client.getFirstName());
            preparedStatement.setString(2, client.getLastName());
            preparedStatement.setString(3, client.getTelephone());
            preparedStatement.setInt(4, client.getId());
            preparedStatement.execute();
            // Clear all previous extra associations (even the ones that are still valid, since we're adding them again)
            preparedStatement = connection.prepareStatement("DELETE FROM rentalextras WHERE rental_id = ?");
            preparedStatement.setInt(1, rental.getId());
            preparedStatement.execute();

            // Iterate over extraArrayList and associate database entry for each extra present in the collection.
            for (Extra extra : Session.extraSelectionList) {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO rentalextras (rental_id, extra_id) VALUES (?,?);");
                preparedStatement.setInt(1, rental.getId());
                preparedStatement.setInt(2, extra.getId());
                preparedStatement.execute();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Database.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalEditController(this);
        addReactivity();
        Rental rental = Session.rentalEntityList.get(entityIndex);
        Client client = Session.clientEntityList.get(entityIndex);
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        ArrayList<Extra> extraArrayList = Session.rentalExtrasCollectionList.get(entityIndex);
        loadDataIntoFields(rental, client, motorhome, model, brand, extraArrayList);
        seasonChoiceBox.getItems().addAll("Low Season", "Mid Season", "Peak Season");
        FXUtils.formatIntegerFields(distanceField);
        addExtrasButton.setOnAction(actionEvent -> {
            ExtraSelectionEntityController.adding = false;
            FXUtils.popUp("rental_extra_selection", "popup", "Select Extras");
        });
        confirmButton.setOnAction(actionEvent -> {
            updateEntities(rental, client);
            if (!editRental(rental, client)) {
                FXUtils.alert(Alert.AlertType.ERROR, "Something went wrong! Check for errors in the fields.",
                        "Rental Edit", "Edit failed!", true);
            } else {
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            }
        });
    }
}