package com.motorhome.controller.rental;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RentalMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox motorhomeToolFlipper;
    @FXML private HBox clientToolFlipper;
    @FXML private HBox startDateToolFlipper;
    @FXML private HBox endDateToolFlipper;
    @FXML private HBox priceToolFlipper;
    @FXML private HBox settings;
    @FXML private HBox add;

    // String gets changed depending on what order was last used.
    // Can be used to flip order, not using boolean for clarity.
    private String currentOrder;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setRentalMenuController(this);

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        settings.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("rental_settings", "motorhome_settings", "Rental Options");
        });

        backButton.setOnAction(actionEvent -> FXUtils.changeRoot("main_menu","main_menu", backButton));
    }
}
