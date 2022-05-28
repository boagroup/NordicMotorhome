package com.motorhome.controller.staff;

import com.motorhome.controller.MenuController;
import com.motorhome.model.Staff;
import com.motorhome.model.User;
import com.motorhome.persistence.DataResult;
import com.motorhome.persistence.Session;
import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles the logic behind the Staff Menu
 * Author(s): Octavian Roman
 */
public class StaffMenuController extends MenuController {
    // FX nodes
    @FXML private HBox nameToolFlipper;
    @FXML private HBox roleToolFlipper;
    @FXML private HBox phoneToolFlipper;
    @FXML private HBox genderToolFlipper;

    @Override
    public void fetchEntities() {
        fetchEntities("firstName", "ASC");
    }

    /**
     * Fetch the existing Staff Entities from the database and display them in the Menu.
     * @param column Schema column which will be used to order the entities.
     * @param order String which will determine whether the order is ascending or descending: "ASC" or "DESC".
     */
    @Override
    public void fetchEntities(String column, String order) {
    /*
     * 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
     * 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
     * 3. Retrieve Staff and User entities from database and store them in ResultSet. Password needs to be decrypted.
     * 4. Iterate over ResultSet, per iteration:
     *    a) Create objects for each entry of aforementioned entities.
     *    b) Add all objects to their ORM ArrayLists in persistence.Session.
     *    f) Immediately inject a new RentalEntity into the menu. This will trigger the StaffEntityController, which will handle the logic.
     * 5. Set the label displaying the entity count to the amount of Rental objects in ORM to ensure it stays updated over multiple fetches.
     * 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
     */

        // 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
        entityContainer.getChildren().clear();
        // 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
        Session.staffEntityList.clear();
        Session.userEntityList.clear();
        //language=SQL
        String query =
               "SELECT staff.id, firstName, lastName, image, telephone, role, gender, " +
               "users.id, staff_id, username, AES_DECRYPT(password, ?) AS decrypted_password, admin " +
               "FROM staff JOIN users ON staff.id = users.staff_id " +
               "ORDER BY " + column + " " + order + ";";
        // 3. Retrieve Staff and User entities from database and store them in DataResult. Password needs to be decrypted.
        DataResult rslt = db.executeQuery(query, System.getProperty("key"));
        if (rslt == null) { return; }
        while (rslt.next()) {
            // decrypted password is actually an array of bytes (we hold it as blob in DB),
            // so we need to cast it to String
            String password = "";
            if (rslt.getCurrentRow().get("decrypted_password") != null) {
                password = new String((byte[]) rslt.getCurrentRow().get("decrypted_password"));
            }
            //  a) Create objects for each entry of aforementioned entities.
            Staff staff = new Staff(
                    rslt.get(Integer.class, "staff.id"),
                    rslt.get(String.class, "firstName"),
                    rslt.get(String.class, "lastName"),
                    rslt.get(String.class, "image"),
                    rslt.get(String.class, "telephone"),
                    rslt.get(String.class, "role"),
                    rslt.get(String.class, "gender")
            );
            User user = new User(
                    rslt.get(Integer.class, "users.id"),
                    rslt.get(Integer.class, "staff_id"),
                    rslt.get(String.class, "username"),
                    password,
                    rslt.get(Boolean.class, "admin")
            );
            // b) Add all objects to their ORM ArrayLists in persistence.Session.
            Session.staffEntityList.add(staff);
            Session.userEntityList.add(user);
            // f) Immediately inject a new RentalEntity into the menu. This will trigger the StaffEntityController, which will handle the logic.
            FXUtils.injectEntity("staff_entity", entityContainer);
        }
        // 5. Set the label displaying the entity count to the amount of Rental objects in ORM to ensure it stays updated over multiple fetches.
        entityCountLabel.setText(Session.staffEntityList.size() + " Items");
        // 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
        currentOrder = order;
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    @Override
    protected void prepareToolbar() {
        nameToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("firstName"));
        roleToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("role"));
        phoneToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("telephone"));
        genderToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("gender"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setStaffMenuController(this);
        prepare();

        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("staff_add", "Add New Staff");
            fetchEntities();
        });
    }
}