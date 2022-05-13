package com.motorhome.controller;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Staff;
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
import java.util.ResourceBundle;

public class StaffMenuController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private Label entityCountLabel;
    @FXML private HBox nameToolFlipper;
    @FXML private HBox roleToolFlipper;
    @FXML private HBox phoneToolFlipper;
    @FXML private HBox genderToolFlipper;
    @FXML private HBox add;

    private String currentOrder;

    private void fetchStaff(String column, String order) {
        Connection connection = SimpleDatabase.getConnection();
        if (connection != null) {
            try {
                Session.StaffEntityList.clear();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM staff ORDER BY " + column + " " + order + ";");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Staff staff = new Staff(
                            resultSet.getInt("id"),
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("image"),
                            resultSet.getString("telephone"),
                            resultSet.getString("role"),
                            resultSet.getString("gender")
                            );
                    Session.StaffEntityList.add(staff);
                    FXUtils.injectEntity("staff_entity", entityContainer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SimpleDatabase.closeConnection(connection);
                currentOrder = order;
            }
        }
    }

    private void flipOrder(String field) {
        entityContainer.getChildren().clear();
            if (currentOrder.equals("ASC")) {
                fetchStaff(field, "DESC");
            } else fetchStaff(field, "ASC");
    }

    private void prepareStaffToolbar() {
        entityCountLabel.setText(Session.StaffEntityList.size() + " Items");
        nameToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("firstName"));
        roleToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("role"));
        phoneToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("telephone"));
        genderToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("gender"));
    }

    private void addFunctionality() {
        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("staff_add", "Add New Staff");
            entityContainer.getChildren().clear();
            fetchStaff("firstName", "ASC");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setStaffMenuController(this);

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        fetchStaff("firstName", "ASC");

        prepareStaffToolbar();

        addFunctionality();

        backButton.setOnAction(actionEvent -> FXUtils.changeScene(actionEvent, "main_menu", "NMH Main Menu", "main_menu"));
    }
}