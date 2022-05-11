package com.motorhome;

import com.motorhome.persistence.Session;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

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
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getScene().getStylesheets().add(Objects.requireNonNull(FXUtils.class.getResource("/stylesheets/" + stylesheet + ".css")).toExternalForm());
        stage.setMaximized(true);
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenSize.getWidth());
        stage.setHeight(screenSize.getHeight());
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
            FXMLLoader loader = new FXMLLoader(FXUtils.class.getResource(fxmlFile));
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
}
