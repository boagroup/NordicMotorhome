package com.motorhome.controller.motorhome.popup;

import com.motorhome.utilities.FXUtils;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Database;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles creation of Motorhome entities.
 * Author(s): Octavian Roman
 */
public class MotorhomeAddController implements Initializable {
    // FX Nodes
    @FXML private Label title;
    @FXML private ImageView image;
    @FXML private TextField typeField;
    @FXML private Spinner<Integer> bedsSpinner;
    @FXML private ChoiceBox<String> modelChoiceBox;
    @FXML private Label priceLabel;
    @FXML private Button confirmButton;

    // HashMap to store model ids because we want to display model names not ids in the ChoiceBox
    HashMap<String, Integer> modelsMap = new HashMap<>();
    // Variable gets changed if the user selects an image, defaults to placeholder.
    String imageName = "motorhome_placeholder.png";

    // FX String properties that can be modified dynamically as the user picks different models
    private final StringProperty dynamicTitle = new SimpleStringProperty();
    private final StringProperty dynamicPrice = new SimpleStringProperty();

    /**
     * Bind label nodes to StringProperties and make them update when the ChoiceBox hides
     */
    private void makePriceAndTitleDynamic() {
        title.textProperty().bind(dynamicTitle);
        priceLabel.textProperty().bind(dynamicPrice);
        modelChoiceBox.setOnHidden(event -> {
            if (!modelChoiceBox.getValue().equals("Model")) {
                FXUtils.updateTitle(modelChoiceBox, modelsMap, dynamicTitle);
                FXUtils.updatePrice(modelChoiceBox, modelsMap, dynamicPrice);
            }
        });
        dynamicTitle.setValue("New Motorhome");
        dynamicPrice.setValue("0.00 €");
    }

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
        if (selectedFile == null) {
            return;
        }
        // Generate random file name with date-timestamp + file extension and assign it to imageName
        imageName = FXUtils.generateUniqueImageName(selectedFile);

        // Prepare paths for both destinations. DOES NOT WORK IN JAR
        String pathToNewImage = "src/main/resources/assets/motorhomes/" + imageName;
        String pathToCompiledImages = "target/classes/assets/motorhomes/" + imageName;

        // Copy images to aforementioned paths
        FXUtils.copyImageToAssets(selectedFile, pathToNewImage);
        FXUtils.copyImageToAssets(selectedFile, pathToCompiledImages);

        // Set ImageView inside pop-up to reflect picked image. Won't appear if image hasn't been added to target
        Image newImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/motorhomes/" + imageName)).toExternalForm());
        image.setImage(newImage);
    }

    /**
     * Generate motorhome entity to be added to the database in accordance to user entries in the fields.
     * @return the Motorhome object
     */
    private Motorhome generateEntity() {
        Motorhome motorhome = new Motorhome(
                modelsMap.get(modelChoiceBox.getValue()),
                "/assets/motorhomes/" + imageName,
                typeField.getText(),
                bedsSpinner.getValue()
        );
        imageName = "motorhome_placeholder.png";
        return motorhome;
    }

    /**
     * Add motorhome entity to the database.
     * @param motorhome Motorhome object to be added to the database
     * @return true if successful, false otherwise
     */
    private boolean addMotorhome(Motorhome motorhome) {
        // Establish connection
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "INSERT INTO motorhomes (model_id, image, type, beds) " +
                        "VALUES (?,?,?,?);");
            preparedStatement.setInt(1, motorhome.getModel_id());
            preparedStatement.setString(2, motorhome.getImage());
            preparedStatement.setString(3, motorhome.getType());
            preparedStatement.setInt(4, motorhome.getBeds());
            preparedStatement.execute();
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
        FXUtils.setModelOptions(modelChoiceBox, modelsMap);
        FXUtils.prepareBedsSpinner(bedsSpinner, 12);
        image.setOnMouseClicked(mouseEvent -> pickImage());
        makePriceAndTitleDynamic();
        confirmButton.setOnAction(actionEvent -> {
            boolean success = addMotorhome(generateEntity());
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
            if (!success) {
                FXUtils.alert(Alert.AlertType.ERROR,
                        "Check for errors in your fields and try again",
                        "Motorhome not added",
                        "Something went wrong!", true);
            }
        });
    }
}
