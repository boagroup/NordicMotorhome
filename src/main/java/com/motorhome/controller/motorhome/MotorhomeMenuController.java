package com.motorhome.controller.motorhome;

import com.motorhome.FXUtils;
import com.motorhome.persistence.SimpleDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MotorhomeMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox nameToolFlipper;
    @FXML private HBox roleToolFlipper;
    @FXML private HBox phoneToolFlipper;
    @FXML private HBox genderToolFlipper;
    @FXML private HBox settings;
    @FXML private HBox add;

    // String gets changed depending on what order was last used.
    // Can be used to flip order, not using boolean for clarity.
    private String currentOrder;

    private void fetchMotorhomes(String column, String order) {
        Connection connection = SimpleDatabase.getConnection();
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement("");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleDatabase.closeConnection(connection);
            currentOrder = order;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        settings.setOnMouseClicked(mouseEvent -> FXUtils.popUp("motorhome_settings", "motorhome_settings", "Motorhome Options"));

        backButton.setOnAction(actionEvent -> FXUtils.changeRoot( "main_menu", "main_menu", backButton));
    }
}