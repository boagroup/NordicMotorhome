package com.motorhome.controller.staff.entity;

import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import com.motorhome.controller.staff.popup.StaffEditController;
import com.motorhome.model.Staff;
import com.motorhome.persistence.Session;
import com.motorhome.persistence.Database;
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
    // FX Nodes
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
        Alert alert = FXUtils.confirmDeletion("Staff", staff.getFirstName(), staff.getLastName(), image);
        // If confirmation has been received
        if (alert.getResult() == ButtonType.YES) {
            Connection connection = Database.getConnection();
            try {
                // Execute SQL statement. No need to delete User since it will cascade
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement("DELETE FROM staff WHERE id = ?");
                preparedStatement.setInt(1, staff.getId());
                preparedStatement.execute();

                // Refresh the menu
                Bridge.getStaffMenuController().fetchEntities("firstName", "ASC");;

                // Show success alert.
                FXUtils.alert(Alert.AlertType.INFORMATION, "The staff and it's associated user account (if any) has been deleted",
                        "Staff Deleted", "Deletion Successful", true);
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.alert(Alert.AlertType.ERROR, "Error", "Deletion Error", "Something went wrong! (SQL Error)", false);
            } finally {
                Database.closeConnection(connection);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Staff staff = Session.staffEntityList.get(entityIndex);
        Image i = new Image(Objects.requireNonNullElse(getClass().getResource(staff.getImage()),
                getClass().getResource("/assets/users/user_placeholder.png")).toExternalForm());
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
            Bridge.getStaffMenuController().fetchEntities("firstName", "ASC");
        });
        delete.setOnAction(actionEvent -> deleteStaff(staff));
    }
}