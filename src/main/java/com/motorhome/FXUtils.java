package com.motorhome;

import com.motorhome.persistence.Session;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains tools that will be needed in most classes
 * Mostly related to JavaFX
 * Author(s): Octavian Roman
 */
public class FXUtils {

    /**
     * Method that changes current scene to another one.
     * @param event Event that triggers the change of scene (e.g. actionEvent)
     * @param FXMLView View that should be changed to
     * @param title Title of the new scene
     */
    public static void changeScene(Event event, String FXMLView, String title, String stylesheet) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(FXUtils.class.getResource("/view/" + FXMLView + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenSize.getWidth());
        stage.setHeight(screenSize.getHeight());
        stage.setMaximized(false);
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(FXUtils.class.getResource("/stylesheets/" + stylesheet + ".css")).toExternalForm());
        stage.setMaximized(true);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Overloaded method that changes the scene to another one.
     * A more complete version of the previous method.
     * Not needed in most cases, but useful if switching from a maximized window to one that is not.
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
     * @param imagePath Path to the image of the alert; pass empty string for default
     * @param show if true, alert will just show, if false, alert will showAndWait which freezes the application until dealt with
     */
    public static void alert(Alert.AlertType type, String contextText, String title, String header, String imagePath, boolean show) {
        Alert alert = new Alert(type);
        alert.setContentText(contextText);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (!imagePath.equals("")) {
            stage.getIcons().add(new Image(Objects.requireNonNull(FXUtils.class.getResource(imagePath)).toExternalForm()));
        }
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
        Image i = new Image(Session.CurrentUser.getCurrentUser().getImage());
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
     * Generates a unique name for a given file, in our case images
     * Needed because if files were to preserve their name when copied to our directories, then there is a chance
     * that the name could already exist, which would force us to either replace the existing image with the new one,
     * or not to copy it at all and throw an error
     * @param image file for which the random name will be generated
     * @return the random name + appropriate extension
     */
    public static String generateRandomImageName(File image) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        if (getExtension(image.getName()).isPresent()) {
            return formatter.format(LocalDateTime.now()) + "." + getExtension(image.getName()).get();
        } else return null;
    }
}
