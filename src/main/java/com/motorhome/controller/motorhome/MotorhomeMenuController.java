package com.motorhome.controller.motorhome;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.Session;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MotorhomeMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox brandToolFlipper;
    @FXML private HBox modelToolFlipper;
    @FXML private HBox typeToolFlipper;
    @FXML private HBox availabilityToolFlipper;
    @FXML private HBox settings;
    @FXML private HBox add;

    // String gets changed depending on what order was last used.
    // Can be used to flip order, not using boolean for clarity.
    private String currentOrder;

    private void fetchMotorhomes(String column, String order) {
        Connection connection = SimpleDatabase.getConnection();
        try {
            Session.motorhomeEntityList.clear();
            Session.brandEntityList.clear();
            Session.modelEntityList.clear();
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
                    "SELECT * FROM motorhomes " +
                        "JOIN models ON motorhomes.model_id = models.id " +
                        "JOIN brands ON models.brand_id = brands.id " +
                        "ORDER BY " + column + " " + order + ";");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Motorhome motorhome = new Motorhome(
                        resultSet.getInt("motorhomes.id"),
                        resultSet.getInt("model_id"),
                        resultSet.getString("image"),
                        resultSet.getBoolean("rented"),
                        resultSet.getString("type"),
                        resultSet.getInt("beds")
                );
                Brand brand = new Brand(
                        resultSet.getInt("brands.id"),
                        resultSet.getString("brands.name"),
                        resultSet.getDouble("brands.price")
                );
                Model model = new Model(
                        resultSet.getInt("models.id"),
                        resultSet.getInt("brand_id"),
                        resultSet.getString("models.name"),
                        resultSet.getDouble("models.price")
                );
                Session.motorhomeEntityList.add(motorhome);
                Session.brandEntityList.add(brand);
                Session.modelEntityList.add(model);
                FXUtils.injectEntity("motorhome_entity", entityContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleDatabase.closeConnection(connection);
            currentOrder = order;
        }
    }

    /**
     * Flips the order of the entities in the Scene.
     * @param field Field that is selected to flip the order (e.g. "brands.name")
     */
    private void flipOrder(String field) {
        entityContainer.getChildren().clear();
        if (currentOrder.equals("ASC")) {
            fetchMotorhomes(field, "DESC");
        } else fetchMotorhomes(field, "ASC");
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    private void prepareToolbar() {
        entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
        brandToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
        modelToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("models.name"));
        typeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("type"));
        availabilityToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("rented"));
    }

    /**
     * Unload and load the entities to refresh them
     */
    public void refresh() {
        entityContainer.getChildren().clear();
        fetchMotorhomes("brands.name", "ASC");
        entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
    }

    /**
     * Prepare add functionality on add button and refresh on addition
     */
    private void setAddFunctionality() {
        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("motorhome_add", "popup", "Add Motorhome");
            refresh();
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setMotorhomeMenuController(this);
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        fetchMotorhomes("brands.name", "ASC");

        prepareToolbar();

        settings.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("motorhome_settings", "motorhome_settings", "Motorhome Options");
            refresh();
        });

        setAddFunctionality();

        backButton.setOnAction(actionEvent -> FXUtils.changeRoot( "main_menu", "main_menu", backButton));
    }
}