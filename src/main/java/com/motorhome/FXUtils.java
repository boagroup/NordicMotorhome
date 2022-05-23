package com.motorhome;

import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains tools that will be needed in most classes
 * Mostly related to JavaFX
 * Author(s): Octavian Roman
 */
public class FXUtils {

    /**
     * Method that changes the scene to another one.
     * Only needed if changing from maximized to minimized views and vice-versa. (e.g. log out)
     * @param event Event that triggers the change of scene (e.g. actionEvent)
     * @param FXMLView View that should be changed to
     * @param title Title of the new scene
     * @param resizable True if the scene is resizable, false if not
     * @param maximized True if the scene is maximized, false if not
     * @param width Width of the scene. Has no effect if maximized
     * @param height Height of the scene. Has no effect if maximized
     */
    public static void changeScene(Event event, String FXMLView, String title, boolean resizable, boolean maximized, String stylesheet, int width, int height) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(FXUtils.class.getResource("/view/" + FXMLView + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(resizable);
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(FXUtils.class.getResource("/stylesheets/" + stylesheet + ".css")).toExternalForm());
        stage.setMaximized(maximized);
        if (!maximized) {
            stage.setWidth(width);
            stage.setHeight(height);
            stage.centerOnScreen();
        }
        stage.show();
    }

    /**
     * Method that switched from one view to another.
     * Changes the content (scene) of a window (stage).
     * No more weird transitions.
     * @param FXMLView FXML file which represents the view to which we are switching.
     * @param stylesheet Stylesheet associated with the view.
     * @param node Node to retrieve the scene that needs to be switched. Normally you just want to pass the Node (e.g. button) triggering scene change here.
     */
    public static void changeRoot(String FXMLView, String stylesheet, Node node) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(FXUtils.class.getResource("/view/" + FXMLView + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = node.getScene();
        scene.getStylesheets().add(Objects.requireNonNull(FXUtils.class.getResource("/stylesheets/" + stylesheet + ".css")).toExternalForm());
        scene.setRoot(root);
    }

    /**
     * Generates a new pop-up window when called.
     * Rest of the program is not usable until the pop-up is dealt with
     * Not to be confused with Alert
     * Used mainly for add and edit entity functionality
     * @param fxmlFile View of the pop-up
     * @param title Title of the pop-up
     */
    public static void popUp(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(FXUtils.class.getResource("/view/" + fxmlFile + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a new pop-up window when called.
     * Rest of the program is not usable until the pop-up is dealt with
     * Not to be confused with Alert
     * Used mainly for add and edit entity functionality
     * Overloaded to add stylesheet if needed
     * @param fxmlFile View of the pop-up
     * @param title Title of the pop-up
     */
    public static void popUp(String fxmlFile, String stylesheet, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(FXUtils.class.getResource("/view/" + fxmlFile + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(FXUtils.class.getResource("/stylesheets/" + stylesheet + ".css")).toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to display alerts throughout the application.
     * @param type Type of alert; do not use CONFIRMATION as that has more going on and is better to just prepare it manually
     * @param contextText Text that will be displayed in the context area of the alert
     * @param title Title of the alert window
     * @param header Text that will be displayed in the header of the alert
     * @param show if true, alert will just show, if false, alert will showAndWait which freezes the application until dealt with
     */
    public static void alert(Alert.AlertType type, String contextText, String title, String header, boolean show) {
        Alert alert = new Alert(type);
        alert.setContentText(contextText);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(FXUtils.class.getResource("/assets/alt_icon.png")).toExternalForm()));
        if (show) {
            alert.show();
        } else alert.showAndWait();
    }

    /**
     * Set the currently logged user details in the header of a given view.
     * Should be used in the "initialize" function of all views with a header that displays the user.
     * @param usernameLabel Label where the user (staff) firstname and lastname will be inserted
     * @param userImage ImageView where the user (staff) image will be inserted
     */
    public static void setUserDetailsInHeader(Label usernameLabel, ImageView userImage) {
        usernameLabel.setText(Session.CurrentUser.getCurrentUser().getFirstname().concat(" ").concat(Session.CurrentUser.getCurrentUser().getLastname()));
        Image i = new Image(Objects.requireNonNullElse(FXUtils.class.getResource(Session.CurrentUser.getCurrentUser().getImage()),
                FXUtils.class.getResource("/assets/users/user_placeholder.png")).toExternalForm());
        userImage.setImage(i);
    }

    /**
     * Inject entity into Pane. Pane can be anything, although in our case it's always a VBox.
     * Facilitates adding as many entities as needed into a given container in a dynamic way.
     * @param fxmlFile fxml file defining the UI element corresponding to the entity
     * @param entityContainer Pane where the entity is to be injected
     */
    public static void injectEntity(String fxmlFile, Pane entityContainer) {
        try {
            new FXMLLoader();
            Parent root = FXMLLoader.load(Objects.requireNonNull(FXUtils.class.getResource("/view/" + fxmlFile + ".fxml")));
            entityContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a given file to a destination.
     * We only use it for images, destination currently does not work in JAR.
     * @param file file to be copied
     * @param destination destination of where the file should be copied to
     */
    public static void copyImageToAssets(File file, String destination) {
        try {
            Files.copy(file.toPath(), Path.of(destination));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the file extension of a given file.
     * From https://www.baeldung.com/java-file-extension
     * @param filename name of the file
     * @return the file extension (e.g. "png")
     */
    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * Generates a unique name for a given file, in our case images.
     * Needed because if files were to preserve their name when copied to our directories, then there is a chance
     * that the name could already exist, which would force us to either replace the existing image with the new one,
     * or not to copy it at all and throw an error.
     * @param image file for which the random name will be generated
     * @return the random name + appropriate extension
     */
    public static String generateRandomImageName(File image) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        if (getExtension(image.getName()).isPresent()) {
            return formatter.format(LocalDateTime.now()) + "." + getExtension(image.getName()).get();
        } else return null;
    }

    /**
     * Opens a window that allows the user to select an image.
     * @param nodeToGetScene Node whose parent Scene will define on top of where the window will appear
     * @return the picked file
     */
    public static File imagePicker(Node nodeToGetScene) {
        // Prepare file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG images","*.png"),
                new FileChooser.ExtensionFilter("JPG images", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF images", "*.gif")
        );
        // Show file chooser
        Stage stage = (Stage) nodeToGetScene.getScene().getWindow();
        // Pick file
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Restricts the text of a certain field to be appropriate for currencies only, using regex.
     * There is room for improvement, but not all regex seem to work here.
     * @param field TextField node to restrict using regex
     */
    public static void formatCurrencyFields(TextField field) {
        field.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d.\\d*")) {
                field.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

    /**
     * Takes a double and transforms it into a string that will always have two decimal places,
     * as it is appropriate for currencies.
     * @param value double to be formatted
     * @return String containing the double formatted as currency
     */
    public static String formatCurrencyValues(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(value);
    }

    /**
     * Queries the database to retrieve the model options available in motorhome add and edit popups.
     * Adds all options to a ChoiceBox so that the user may select the desired model.
     * Also Stores all model IDs in a HashMap for usage during confirmation; to avoid a database query.
     * @param choiceBox ChoiceBox where all the model options will be inserted
     * @param modelsMap HashMap where the name will act as key and id as value
     */
    public static void setModelOptions(ChoiceBox<String> choiceBox, HashMap<String, Integer> modelsMap) {
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT id, name FROM models;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                choiceBox.getItems().add(name);
                modelsMap.put(name, id);
            }
            choiceBox.setValue("Model");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
    }

    /**
     * Set the dynamicTitle variable to the model and brand names of a given motorhome model and brand.
     * Used in motorhome add and edit popups.
     * The title label is bound to this StringProperty variable, thus it changes when we change it.
     * Unidirectional data flow.
     * @param choiceBox ChoiceBox whose current value is the name of the model to which we want to update the title to
     * @param modelsMap HashMap that setModelOptions above fills in so that we can get the ID from the model name
     * @param dynamicTitle StringProperty to be updated
     */
    public static void updateTitle(ChoiceBox<String> choiceBox, HashMap<String, Integer> modelsMap, StringProperty dynamicTitle) {
        int modelId = modelsMap.get(choiceBox.getValue());
        String brandName = "Something Went ";
        String modelName = "Wrong";
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT brands.name, models.name FROM models JOIN brands ON brand_id = brands.id WHERE models.id = ?;");
            preparedStatement.setInt(1, modelId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            brandName = resultSet.getString("brands.name");
            modelName = resultSet.getString("models.name");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
        dynamicTitle.setValue(brandName + " " + modelName);
    }

    /**
     * Set the dynamicPrice variable to the model and brand prices of a given motorhome model and brand.
     * Used in motorhome add and edit popups.
     * The price label is bound to this StringProperty variable, thus it changes when we change it.
     * Unidirectional data flow.
     * @param choiceBox ChoiceBox whose current value is the name of the model to which we want to update the title to
     * @param modelsMap HashMap that setModelOptions fills in so that we can get the ID from the model name
     * @param dynamicPrice StringProperty to be updated
     */
    public static void updatePrice(ChoiceBox<String> choiceBox, HashMap<String, Integer> modelsMap, StringProperty dynamicPrice) {
        int modelId = modelsMap.get(choiceBox.getValue());
        double brandPrice = 0.0;
        double modelPrice = 0.0;
        Connection connection = Database.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT brands.price, models.price FROM models JOIN brands ON brand_id = brands.id WHERE models.id = ?;");
            preparedStatement.setInt(1, modelId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            brandPrice = resultSet.getDouble("brands.price");
            modelPrice = resultSet.getDouble("models.price");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.closeConnection(connection);
        }
        dynamicPrice.setValue(FXUtils.formatCurrencyValues(brandPrice + modelPrice) + " €");
    }

    /**
     * Prepare any Spinner to have a given amount of integer options starting from 0.
     * Will start from 0 and go up to a given number.
     * @param cap highest value of the Spinner's options
     */
    public static void prepareBedsSpinner(Spinner<Integer> spinner, int cap) {
        ObservableList<Integer> values = FXCollections.observableArrayList();
        for (int i = 0; i <= cap; i++) {
            values.add(i);
        }
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(values);
        spinner.setValueFactory(valueFactory);
    }
}
