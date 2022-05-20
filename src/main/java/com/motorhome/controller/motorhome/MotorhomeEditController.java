package com.motorhome.controller.motorhome;

import com.motorhome.FXUtils;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *  * Handles edition of Motorhome entities.
 *  * Author(s): Octavian Roman
 */
public class MotorhomeEditController implements Initializable {

    @FXML private Label title;
    @FXML private ImageView image;
    @FXML private TextField typeField;
    @FXML private Spinner<Integer> bedsSpinner;
    @FXML private ChoiceBox<String> modelChoiceBox;
    @FXML private Label priceLabel;
    @FXML private Button confirmButton;

    // Integer that gets changed to the appropriate value every time the user clicks on the "Edit" option of an entity
    // -1 to get exception if something goes wrong instead of unwanted entity
    public static int entityIndex = -1;

    // HashMap to store model ids because we want to display model names not ids in the ChoiceBox, saves us from another DB query
    HashMap<String, Integer> modelsMap = new HashMap<>();
    // Variable gets changed if the user selects an image, defaults to placeholder.
    String imageName = "motorhome_placeholder.png";

    // FX String properties that can be modified dynamically as the user picks different models
    private final StringProperty dynamicTitle = new SimpleStringProperty();
    private final StringProperty dynamicPrice = new SimpleStringProperty();

    /**
     * Image picker.
     * Picks an image, generates random name for it, and puts it in assets/motorhomes/ both in compiled and source directories.
     * BROKEN IN JAR (STILL)
     * Last error to fix is being able to point to assets/motorhomes both in src/main and target in JAR files.
     * Can't find how to point to directory inside JAR, only files.
     */
    private void pickImage() {
        File selectedFile = FXUtils.imagePicker(image);
        // Stop execution if no file is selected
        if (!selectedFile.exists()) {
            return;
        }
        // Generate random file name with date-timestamp + file extension and assign it to imageName
        imageName = FXUtils.generateRandomImageName(selectedFile);

        // Prepare paths for both destinations. DOES NOT WORK IN JAR
        String pathToNewImage = "src/main/resources/assets/motorhomes/" + imageName;
        String pathToCompiledImages = "target/classes/assets/motorhomes/" + imageName;

        String imagePath = "/assets/motorhomes/" + imageName;

        // Copy images to aforementioned paths. Just realized try/catch might work in JAR since there's no src. Will try tomorrow
        try {
            FXUtils.copyImageToAssets(selectedFile, pathToNewImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FXUtils.copyImageToAssets(selectedFile, pathToCompiledImages);

        // Set ImageView inside pop-up to reflect picked image. Won't appear if image hasn't been added to target
        Image newImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/motorhomes/" + imageName)).toExternalForm());
        image.setImage(newImage);
        imageName = imagePath;
    }

    /**
     * Bind label nodes to StringProperties and make them update when the ChoiceBox hides
     */
    private void makePriceAndTitleDynamic() {
        title.textProperty().bind(dynamicTitle);
        priceLabel.textProperty().bind(dynamicPrice);
        modelChoiceBox.setOnHidden(event -> {
                FXUtils.updateTitle(modelChoiceBox, modelsMap, dynamicTitle);
                FXUtils.updatePrice(modelChoiceBox, modelsMap, dynamicPrice);
        });
    }

    /**
     * Retrieve motorhome data for a given entity and load it into the fields.
     * Dynamic labels (title + price) also need to be set initially, hence the SQL statements
     * @param motorhome Motorhome object representing the entity whose data is to be loaded into the fields
     */
    private void loadDataIntoFields(Motorhome motorhome) {
        // Retrieve image and create new FX image node with it
        Image image = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        // Put node into actual ImageView
        this.image.setImage(image);
        // Set typeField text to previous type
        typeField.setText(motorhome.getType());
        // Set beds spinner value to previous bed amount
        bedsSpinner.getValueFactory().setValue(motorhome.getBeds());
        // Time to update the title, price, and model inside the box; we need brands and models here too, so we have to query the database
        Connection connection = SimpleDatabase.getConnection();
        try {
            // Get brand and model names
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT brands.name, models.name, brands.price, models.price FROM models JOIN brands ON brand_id = brands.id WHERE models.id = ?;");
            preparedStatement.setInt(1, motorhome.getModel_id());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            // Set model ChoiceBox value to model name
            modelChoiceBox.setValue(resultSet.getString("models.name"));
            String brandName = resultSet.getString("brands.name");
            String modelName = resultSet.getString("models.name");
            // Set title to brand name and model name
            dynamicTitle.setValue(brandName + " " + modelName);
            // Finally, update the price too
            double brandPrice = resultSet.getDouble("brands.price");
            double modelPrice = resultSet.getDouble("models.price");
            dynamicPrice.setValue(FXUtils.formatCurrencyValues(brandPrice + modelPrice) + " €");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleDatabase.closeConnection(connection);
        }
    }

    /**
     * Takes a Motorhome object and sets it to the values provided in the FX nodes.
     */
    private void updateEntity(Motorhome motorhome) {
       motorhome.setImage(imageName);
       motorhome.setModel_id(modelsMap.get(modelChoiceBox.getValue()));
       motorhome.setType(typeField.getText());
       motorhome.setBeds(bedsSpinner.getValue());
    }

    /**
     * Updates a given entity with data from an updated entity.
     * @param motorhome Motorhome object already updated by "updateEntity".
     * @return true if successful, false otherwise
     */
    private boolean editMotorhome(Motorhome motorhome) {
        Connection connection = SimpleDatabase.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "UPDATE motorhomes SET model_id = ?, image = ?, type = ?, beds = ? " +
                        "WHERE id = ?;");
            preparedStatement.setInt(1, motorhome.getModel_id());
            preparedStatement.setString(2, motorhome.getImage());
            preparedStatement.setString(3, motorhome.getType());
            preparedStatement.setInt(4, motorhome.getBeds());
            preparedStatement.setInt(5, motorhome.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            SimpleDatabase.closeConnection(connection);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Store appropriate motorhome object from ArrayList here
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        imageName = motorhome.getImage();
        // Set imagePicker
        image.setOnMouseClicked(mouseEvent -> pickImage());
        // Retrieve available model options and also store them in a HashMap since we are displaying names but need IDs
        FXUtils.setModelOptions(modelChoiceBox, modelsMap);
        // Set spinner options from 0 to 12
        FXUtils.prepareBedsSpinner(bedsSpinner, 12);
        // Make price and title change as user changes model
        makePriceAndTitleDynamic();
        // Retrieve data for given entity and put it into all the fields
        loadDataIntoFields(motorhome);
        // Make confirm button work
        confirmButton.setOnAction(actionEvent -> {
            // Update the object we created at the beginning with whatever is in the fields
            updateEntity(motorhome);
            // Try to update the actual database entity with the object's attributes, also check for success
            if (!editMotorhome(motorhome)) {
                // If it fails, show error
                FXUtils.alert(Alert.AlertType.ERROR, "Something went wrong! Check for errors in the fields.",
                        "Motorhome Edit", "Edit failed!", true);
            }
            // Close the edit window
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
        });
    }
}
