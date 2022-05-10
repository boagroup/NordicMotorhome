package com.motorhome;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 * Contains tools that will be needed in most classes
 * Mostly related to JavaFX
 * Author(s): Octavian Roman
 */
public class Utilities {

    /**
     * Method that changes current scene to another one.
     * @param event Event that triggers the change of scene (e.g. actionEvent)
     * @param FXMLView View that should be changed to
     * @param title Title of the new scene
     */
    public static void changeScene(Event event, String FXMLView, String title) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(Utilities.class.getResource("/view/" + FXMLView + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Method that changes the scene to another one.
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
    public static void changeScene(Event event, String FXMLView, String title, boolean resizable, boolean maximized, int width, int height) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(Utilities.class.getResource("/view/" + FXMLView + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(maximized);
        if (!maximized) {
            stage.setWidth(width);
            stage.setHeight(height);
            stage.centerOnScreen();
        }
        stage.setTitle(title);
        stage.setResizable(resizable);
        stage.setScene(new Scene(Objects.requireNonNull(root)));
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
            FXMLLoader loader = new FXMLLoader(Utilities.class.getResource(fxmlFile));
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
}
