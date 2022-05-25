package com.motorhome.controller.rental;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalAddController implements Initializable {

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
    @FXML private Button pickMotorhomeButton;
    @FXML private Button addExtrasButton;
    @FXML private Button confirmButton;

    // integer gets changed via Bridge as the user picks different motorhomes in their pop-up
    public int motorhomeId = 0;

    // FX String properties that can be modified dynamically as the user modifies the rental characteristics
    private final StringProperty dynamicTitle = new SimpleStringProperty();
    private final StringProperty dynamicDailyPrice = new SimpleStringProperty();
    private final StringProperty dynamicTotalPrice = new SimpleStringProperty();

    /**
     * Prepare choiceBox, set initial values, restrict distance field to integers, disable confirm button
     */
    private void initializeValues() {
        seasonChoiceBox.getItems().addAll("Low Season", "Mid Season", "Peak Season");
        seasonChoiceBox.setValue("Low Season");
        dynamicTitle.setValue("New Rental");
        dynamicDailyPrice.setValue("0.00 €");
        dynamicTotalPrice.setValue("0.00 €");
        FXUtils.formatIntegerFields(distanceField);
        confirmButton.setDisable(true);
    }

    /**
     * Retrieves Motorhome, Model and Brand entities from database in accordance to selected motorhome.
     * Use aforementioned entities to generate objects for calculations
     * @return array with Motorhome at index 0 Model at index 1 and Brand at index 2
     */
    private Object[] retrieveMotorhomeEntities() {
        Object[] result = new Object[3];
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
            "SELECT * FROM motorhomes " +
                "JOIN models ON motorhomes.model_id = models.id " +
                "JOIN brands ON models.brand_id = brands.id " +
                "WHERE motorhomes.id = ?;");
            preparedStatement.setInt(1, motorhomeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Motorhome motorhome = new Motorhome(
                resultSet.getInt("motorhomes.id"),
                resultSet.getInt("models.id"),
                resultSet.getString("image"),
                resultSet.getBoolean("rented"),
                resultSet.getString("type"),
                resultSet.getInt("beds")
            );
            result[0] = motorhome;
            Model model = new Model(
                resultSet.getInt("models.id"),
                resultSet.getInt("brand_id"),
                resultSet.getString("models.name"),
                resultSet.getDouble("models.price")
            );
            result[1] = model;
            Brand brand = new Brand(
                    resultSet.getInt("brands.id"),
                    resultSet.getString("brands.name"),
                    resultSet.getDouble("brands.price")
            );
            result[2] = brand;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            Database.closeConnection(connection);
        }
        return result;
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
            Object[] entityArray = retrieveMotorhomeEntities();
            Model model = (Model) Objects.requireNonNull(entityArray)[1];
            Brand brand = (Brand) Objects.requireNonNull(entityArray)[2];
            updateDynamicFields(model, brand);
        }
    }

    /**
     * Gets called from the Bridge when the user selects an available Motorhome from the "Pick Motorhome" pop-up.
     * Sets the local variable ID to the appropriate motorhome ID, also updates all the fields and image accordingly.
     * Enables the submit button (Cannot submit a rental with no motorhome)
     * @param motorhomeId ID integer that is provided when any motorhome is selected in the pop-up
     */
    public void pickMotorhome(int motorhomeId) {
        this.motorhomeId = motorhomeId;
        Object[] entityArray = retrieveMotorhomeEntities();
        Motorhome motorhome = (Motorhome) Objects.requireNonNull(entityArray)[0];
        Model model = (Model) Objects.requireNonNull(entityArray)[1];
        Brand brand = (Brand) Objects.requireNonNull(entityArray)[2];
        updateDynamicFields(model, brand);
        Image newImage = new Image(Objects.requireNonNull(getClass().getResource(motorhome.getImage())).toExternalForm());
        image.setImage(newImage);
        confirmButton.setDisable(false);
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
     * Generates Rental and Client entities that will be added to the database.
     * @param finalPrice Final Price needs to be computed before a new rental can be added, and is provided here
     * @return array with Rental at index 0 and Client at index 1
     */
    private Object[] generateEntities(double finalPrice) {
        String seasonEnum =  "";
        switch (seasonChoiceBox.getValue()) {
            case "Low Season" -> seasonEnum = "L";
            case "Mid Season" -> seasonEnum = "M";
            case "Peak Season" -> seasonEnum = "P";
        }
        Rental rental = new Rental(
                motorhomeId,
                Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                pickUpLocationField.getText(),
                seasonEnum,
                java.sql.Date.valueOf(startDateDatePicker.getValue()),
                java.sql.Date.valueOf(endDateDatePicker.getValue()),
                finalPrice,
                notes.getText()
        );
        Client client = new Client(
                0,
                clientFirstNameField.getText().equals("") ? null : clientFirstNameField.getText(),
                clientLastNameField.getText(),
                clientTelephoneField.getText()
        );
        Object[] result = new Object[2];
        result[0] = rental;
        result[1] = client;
        return result;
    }

    /**
     * Adds rental, client and rentalExtras entities to the database.
     * 1. Compute final price.
     * 2. Generate entities with field data and computed price.
     * 3. Insert rental entity into database.
     * 4. Save newly inserted rental entity ID into local variable.
     * 5. Insert client entity into database and associate it with rental using the retrieved ID.
     * 6. Iterate over extraArrayList and add database entry for each extra present in the collection.
     * 7. Set motorhome which has been just rented "rented" boolean to true, since it is not available anymore.
     * @return true if successful, false otherwise
     */
    private boolean addRental() {
        Connection connection = Database.getConnection();
        try {
            // Compute final price.
            Object[] oldEntityArray = retrieveMotorhomeEntities();
            Model model = (Model) Objects.requireNonNull(oldEntityArray)[1];
            Brand brand = (Brand) Objects.requireNonNull(oldEntityArray)[2];
            double dailyPrice = FXUtils.computeDailyPrice(brand, model, seasonChoiceBox);
            double finalPrice = FXUtils.computeFinalPrice(
                    dailyPrice,
                    java.sql.Date.valueOf(startDateDatePicker.getValue()),
                    java.sql.Date.valueOf(endDateDatePicker.getValue()),
                    Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                    Session.extraSelectionList);

            // Generate entities with field data and computed price.
            Object[] newEntityArray = generateEntities(finalPrice);
            Rental rental = (Rental) newEntityArray[0];
            Client client = (Client) newEntityArray[1];

            // Insert rental entity into database.
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO rentals (motorhome_id, distance, season, start_date, end_date, final_price, notes) " +
                        "VALUES (?,?,?,?,?,?,?);");
            preparedStatement.setInt(1, rental.getMotorhome_id());
            preparedStatement.setInt(2, rental.getDistance());
            preparedStatement.setString(3, rental.getSeason());
            preparedStatement.setDate(4, rental.getStart_date());
            preparedStatement.setDate(5, rental.getEnd_date());
            preparedStatement.setDouble(6, rental.getFinal_price());
            preparedStatement.setString(7, rental.getNotes());
            preparedStatement.execute();

            // Save newly inserted rental entity ID into local variable.
            preparedStatement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int id = resultSet.getInt(1);

            // Insert client entity into database and associate it with rental using the retrieved ID.
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO clients (rental_id, firstName, lastName, telephone) " +
                        "VALUES (?,?,?,?);");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, client.getFirstName());
            preparedStatement.setString(3, client.getLastName());
            preparedStatement.setString(4, client.getTelephone());
            preparedStatement.execute();

            // Iterate over extraArrayList and associate database entry for each extra present in the collection.
            for (Extra extra : Session.extraSelectionList) {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO rentalextras (rental_id, extra_id) VALUES (?,?);");
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, extra.getId());
                preparedStatement.execute();
            }

            // Set motorhome which has been just rented "rented" boolean to true, since it is not available anymore.
            preparedStatement = connection.prepareStatement(
                    "UPDATE motorhomes SET rented = 1 WHERE id = ?");
            preparedStatement.setInt(1, rental.getMotorhome_id());
            preparedStatement.execute();
            return true;
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return false;
        } finally {
            Database.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalAddController(this);
        MotorhomeSelectionEntityController.controlFlipper = true;
        initializeValues();
        addReactivity();
        // Pick motorhome button and image do the same thing. You can only change motorhome image in motorhome menu.
        pickMotorhomeButton.setOnAction(actionEvent -> FXUtils.popUp("rental_motorhome_selection", "motorhome_menu", "Select Motorhome"));
        image.setOnMouseClicked(mouseEvent -> FXUtils.popUp("rental_motorhome_selection", "motorhome_menu", "Select Motorhome"));
        // Reset previous extras (if any) before showing pop-up
        addExtrasButton.setOnAction(actionEvent -> {
            ExtraSelectionEntityController.adding = true;
            Session.extraSelectionList.clear();
            FXUtils.popUp("rental_extra_selection", "popup", "Select Extras");
        });
        confirmButton.setOnAction(actionEvent -> {
            boolean success = addRental();
            if (!success) {
                FXUtils.alert(Alert.AlertType.ERROR,
                        "Check for errors in your fields and try again",
                        "Rental not added",
                        "Something went wrong!", true);
            } else {
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            }
        });
    }
}