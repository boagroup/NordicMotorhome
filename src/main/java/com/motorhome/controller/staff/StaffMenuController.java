package com.motorhome.controller.staff;

import com.motorhome.Bridge;
import com.motorhome.FXUtils;
import com.motorhome.model.Staff;
import com.motorhome.model.User;
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
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the Staff Menu
 * Author(s): Octavian Roman
 */
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

    // String gets changed depending on what order was last used.
    // Can be used to flip order, not using boolean for clarity.
    private String currentOrder;

    /**
     * Fetch all Staff and User entities and inject them into the entity container.
     * The container is hosted by a ScrollPane, which sets no hard limit on how many entities can be injected.
     * Can flip order of entities with parameters.
     * Loads entities into ArrayLists in the Session class for further manipulation down the line.
     * There is no need for more complex Session storage methods since fetching is iterative, meaning that the order
     * of the ArrayList will always be correct, considering all Staff entities must have a User entity associated,
     * even though said user entity does not necessarily grant access to the system.
     * @param column Column which will determine the order of injection.
     * @param order "ASC" or "DESC", determines ascending or descending order.
     */
    private void fetchStaff(String column, String order) {
        Connection connection = SimpleDatabase.getConnection();
        try {
            // Clear arraylists to be safe
            Session.staffEntityList.clear();
            Session.userEntityList.clear();
            // Select everything from both tables, long statement because we need to decrypt the password
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(
            "SELECT staff.id, firstName, lastName, image, telephone, role, gender, " +
                "users.id, staff_id, username, AES_DECRYPT(password, ?) AS decrypted_password, admin " +
                "FROM staff JOIN users ON staff.id = users.staff_id " +
                "ORDER BY " + column + " " + order + ";");
            preparedStatement.setString(1, System.getProperty("key"));
            ResultSet resultSet = preparedStatement.executeQuery();
            // Iterate over each entity in the result set, and create Staff and User objects with each one of them
            while (resultSet.next()) {
                Staff staff = new Staff(
                        resultSet.getInt("staff.id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("image"),
                        resultSet.getString("telephone"),
                        resultSet.getString("role"),
                        resultSet.getString("gender")
                        );
                User user = new User(
                        resultSet.getInt("users.id"),
                        resultSet.getInt("staff_id"),
                        resultSet.getString("username"),
                        resultSet.getString("decrypted_password"),
                        resultSet.getBoolean("admin")
                );
                // Add objects as soon as they are created, ensuring correct order
                Session.staffEntityList.add(staff);
                Session.userEntityList.add(user);
                // Inject what we just inserted into the ArrayLists into the scene
                FXUtils.injectEntity("staff_entity", entityContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleDatabase.closeConnection(connection);
            // Store what order we just fetched with to be able to flip it later, if needed
            currentOrder = order;
        }
    }

    /**
     * Flips the order of the entities in the Scene.
     * @param field Field that is selected to flip the order (e.g. "firstName")
     */
    private void flipOrder(String field) {
        entityContainer.getChildren().clear();
            if (currentOrder.equals("ASC")) {
                fetchStaff(field, "DESC");
            } else fetchStaff(field, "ASC");
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    private void prepareStaffToolbar() {
        entityCountLabel.setText(Session.staffEntityList.size() + " Items");
        nameToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("firstName"));
        roleToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("role"));
        phoneToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("telephone"));
        genderToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("gender"));
    }

    /**
     * Inserts add functionality onto the "add" button.
     * Logic is handled by an external view.
     */
    private void addFunctionality() {
        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("staff_add", "Add New Staff");
            refresh();
        });
    }

    /**
     * Method that clears the entity container and fills it again.
     * Used to refresh the entities.
     * Public to use it from other controllers with Bridge.
     */
    public void refresh() {
        entityContainer.getChildren().clear();
        fetchStaff("firstName", "ASC");
        entityCountLabel.setText(Session.staffEntityList.size() + " Items");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setStaffMenuController(this);

        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);

        fetchStaff("firstName", "ASC");

        prepareStaffToolbar();

        addFunctionality();

        backButton.setOnAction(actionEvent -> FXUtils.changeRoot("main_menu", "main_menu", backButton));
    }
}