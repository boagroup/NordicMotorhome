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

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalEditController implements Initializable {

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

    public static int entityIndex = -1;
    // integer gets changed via Bridge as the user picks different motorhomes in their pop-up
    public int motorhomeId = 0;
    // ArrayList is modified as the user checks or unchecks boxes for each available extra
    public ArrayList<Extra> extraArrayList = new ArrayList<>();

    // FX String properties that can be modified dynamically as the user modifies the rental characteristics
    private final StringProperty dynamicTitle = new SimpleStringProperty();
    private final StringProperty dynamicDailyPrice = new SimpleStringProperty();
    private final StringProperty dynamicTotalPrice = new SimpleStringProperty();

    /**
     * Retrieve motorhome data for a given entity and load it into the fields.
     * Dynamic labels (title + price) also need to be set initially, hence the SQL statements
     * @param motorhome Motorhome object representing the entity whose data is to be loaded into the fields
     */
    private void loadDataIntoFields(Rental rental, Client client, Motorhome motorhome, Model model, Brand brand) {
        clientFirstNameField.setText(client.getFirstName());
        clientLastNameField.setText(client.getLastName());
        clientTelephoneField.setText(client.getTelephone());
        String season = "Season";
        switch (rental.getSeason()) {
            case "L" -> season = "Low Season";
            case "M" -> season = "Mid Season";
            case "H" -> season = "High Season";
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
        motorhomeId = motorhome.getId();
        updateDynamicFields(model, brand);
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
                            extraArrayList)) + " €");
        } else {  // Same, but if no dates have been picked
            dynamicTotalPrice.setValue(
                    FXUtils.formatCurrencyValues(FXUtils.computeFinalPrice(
                            dailyPrice,
                            Integer.parseInt(distanceField.getText().equals("") ? "0" : distanceField.getText()),
                            extraArrayList)) + " €");
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

    private void initializeValues(int entityIndex) {
        Rental rental = Session.rentalEntityList.get(entityIndex);
        Client client = Session.clientEntityList.get(entityIndex);
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        loadDataIntoFields(rental, client, motorhome, model, brand);
        seasonChoiceBox.getItems().addAll("Low Season", "Mid Season", "High Season");
        FXUtils.formatIntegerFields(distanceField);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalEditController(this);
        MotorhomeSelectionEntityController.controlFlipper = false;
        addReactivity();
        initializeValues(entityIndex);
        addExtrasButton.setOnAction(actionEvent -> {
            extraArrayList.clear();
            reflectFieldChanges();
            FXUtils.popUp("rental_extra_selection", "popup", "Select Extras");
        });
    }
}
