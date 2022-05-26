package com.motorhome;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Called by the entry point
 * Initializes the first view of the application (Authentication)
 * Author(s): Octavian Roman
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Create the scene and link it with it's corresponding controller
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/authentication.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        // Link scene to compartmentalized stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheets/authentication.css")).toExternalForm());
        // Set window icon; This actually works in JAR, as opposed to dealing with plain files, which means all image creation will be done so from here onwards
        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/assets/icon128.png")).toExternalForm());
        stage.getIcons().add(icon);
        // Make window not resizable
        stage.setResizable(false);
        // Set window title
        stage.setTitle("NMH Authentication");
        // Show the scene
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}