package com.motorhome.controller.Entity;

import com.motorhome.Bridge;
import com.motorhome.FXTools.FXUtils;
import com.motorhome.controller.PopUp.StaffEditController;
import com.motorhome.model.Staff;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.SimpleDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles logic of each Staff entity appearing in the Staff Menu
 * Author(s): Octavian Roman
 */
public class StaffEntityController implements Initializable {
    @FXML private ImageView image;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label genderLabel;
    @FXML private MenuItem edit;
    @FXML private MenuItem delete;

    // This class is always going to insert the last entity inside the ArrayList.
    // The same index for User since they get inserted simultaneously.
    public final int entityIndex = Session.staffEntityList.size() - 1;

    /**
     * Function providing delete functionality.
     * Throws error if attempting self-deletion.
     * Cascades to the User too.
     * @param staff Staff object representing the entity to be deleted.
     */
    private void deleteStaff(Staff staff) {
        if (staff.getId() == Session.CurrentUser.getCurrentUser().getStaff_ID()) {
            FXUtils.alert(Alert.AlertType.ERROR, "Error", "Deletion Error", "You cannot delete yourself!",false);
            return;
        }
        // Get confirmation with an alert. No function for this since they'd have to take too many arguments anyways.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete "+ nameLabel.getText() + "?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Please Confirm Staff Deletion");
        alert.setTitle("Staff Deletion");
        // Has to be done like this
        // Plain alert.setGraphic(image) removes the image from the child instance node
        ImageView tempImage = new ImageView();
        tempImage.setImage(image.getImage());
        // Must explicitly size image, otherwise it preserves original size and makes pop-up grow
        tempImage.setFitHeight(64.0);
        tempImage.setFitWidth(64.0);
        alert.setGraphic(tempImage);
        Stage popStage = (Stage) alert.getDialogPane().getScene().getWindow();
        popStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/assets/alt_icon.png")).toExternalForm()));
        alert.showAndWait();

        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = SimpleDatabase.getConnection();
            try {
                // Execute SQL statement. No need to delete User since it will cascade
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement("DELETE FROM staff WHERE id = ?");
                preparedStatement.setInt(1, staff.getId());
                preparedStatement.execute();

                // Refresh the menu
                Bridge.getStaffMenuController().refresh();

                // Show success alert.
                FXUtils.alert(Alert.AlertType.INFORMATION, "The staff and it's associated user account (if any) has been deleted",
                        "Staff Deleted", "Deletion Successful", true);

            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.alert(Alert.AlertType.ERROR, "Error", "Deletion Error", "Something went wrong! (SQL Error)", false);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Staff staff = Session.staffEntityList.get(entityIndex);
        Image i = new Image(Objects.requireNonNull(getClass().getResource(staff.getImage())).toExternalForm());
        image.setImage(i);
        nameLabel.setText(staff.getFirstName() + " " + staff.getLastName());
        roleLabel.setText(staff.getRole());
        telephoneLabel.setText(staff.getTelephone());
        genderLabel.setText(staff.getGender());
        if (!staff.getGender().equals("")) {
            switch (staff.getGender()) {
                case "M" -> genderLabel.setText("Male");
                case "F" -> genderLabel.setText("Female");
                case "N" -> genderLabel.setText("Non-Binary");
                case "D" -> genderLabel.setText("Decline to State");
            }
        }
        edit.setOnAction(actionEvent -> {
            StaffEditController.entityIndex = entityIndex;
            FXUtils.popUp("staff_edit", "Edit Staff");
            Bridge.getStaffMenuController().refresh();
        });
        delete.setOnAction(actionEvent -> deleteStaff(staff));
    }
}