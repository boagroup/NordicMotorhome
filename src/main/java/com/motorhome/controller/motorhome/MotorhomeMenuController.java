package com.motorhome.controller.motorhome;

import com.motorhome.controller.MenuController;
import com.motorhome.model.Brand;
import com.motorhome.model.Model;
import com.motorhome.model.Motorhome;
import com.motorhome.persistence.DataResult;
import com.motorhome.persistence.Session;
import com.motorhome.utilities.Bridge;
import com.motorhome.utilities.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MotorhomeMenuController extends MenuController {

    @FXML private HBox settings;
    @FXML private HBox brandToolFlipper;
    @FXML private HBox modelToolFlipper;
    @FXML private HBox typeToolFlipper;
    @FXML private HBox availabilityToolFlipper;

    @Override
    public void fetchEntities() {
        fetchEntities("brands.name", "ASC");
    }

    /**
     * Fetch the existing Motorhome Entities from the database and display them in the Menu.
     * 1. Clear container where fetching injection occurs to avoid duplication for multiple fetches.
     * 2. Clear relevant ORM ArrayLists to preserve data integrity over multiple fetches.
     * 3. Retrieve Motorhome, Brand and Model entities from database and store them in ResultSet.
     * 4. Iterate over DataResult, per iteration:
     *    a) Create objects for each entry of aforementioned entities.
     *    b) Add all objects to their ORM ArrayLists in persistence.Session.
     *    f) Immediately inject a new MotorhomeEntity into the menu. This will trigger the MotorhomeEntityController, which will handle the logic.
     * 5. Set the label displaying the entity count to the amount of Motorhome objects in ORM to ensure it stays updated over multiple fetches.
     * 6. Finally, store the order that was used to fetch in order to be able to flip it on demand later.
     * @param column Schema column which will be used to order the entities.
     * @param order String which will determine whether the order is ascending or descending: "ASC" or "DESC".
     */
    @Override
    public void fetchEntities(String column, String order) {
        entityContainer.getChildren().clear();
        Session.motorhomeEntityList.clear();
        Session.brandEntityList.clear();
        Session.modelEntityList.clear();
        //language=SQL
        String query =
                "SELECT * FROM motorhomes " +
                "JOIN models ON motorhomes.model_id = models.id " +
                "JOIN brands ON models.brand_id = brands.id " +
                "ORDER BY " + column + " " + order + ";";
        DataResult rslt = db.executeQuery(query);
        if (rslt == null) {return;}
        while (rslt.next()) {
            Motorhome motorhome = new Motorhome(
                    rslt.get(Integer.class, "motorhomes.id"),
                    rslt.get(Integer.class, "model_id"),
                    rslt.get(String.class, "image"),
                    rslt.get(Boolean.class, "rented"),
                    rslt.get(String.class, "type"),
                    rslt.get(Integer.class, "beds")
            );
            Brand brand = new Brand(
                    rslt.get(Integer.class, "brands.id"),
                    rslt.get(String.class, "brands.name"),
                    rslt.get(Double.class, "brands.price")
            );
            Model model = new Model(
                    rslt.get(Integer.class, "models.id"),
                    rslt.get(Integer.class, "brand_id"),
                    rslt.get(String.class, "models.name"),
                    rslt.get(Double.class, "models.price")
            );
            Session.motorhomeEntityList.add(motorhome);
            Session.brandEntityList.add(brand);
            Session.modelEntityList.add(model);
            FXUtils.injectEntity("motorhome_entity", entityContainer);
        }
        entityCountLabel.setText(Session.rentalEntityList.size() + " Items");
        currentOrder = order;
    }

    /**
     * Load flipping functions into toolbar at the top of the entity container to allow order inversion
     */
    @Override
    protected void prepareToolbar() {
        entityCountLabel.setText(Session.motorhomeEntityList.size() + " Items");
        brandToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("brands.name"));
        modelToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("models.name"));
        typeToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("type"));
        availabilityToolFlipper.setOnMouseClicked(mouseEvent -> flipOrder("rented"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bridge.setMotorhomeMenuController(this);
        prepare();
        settings.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("motorhome_settings", "motorhome_settings", "Motorhome Options");
            fetchEntities();
        });
        add.setOnMouseClicked(mouseEvent -> {
            FXUtils.popUp("motorhome_add", "popup", "Add Motorhome");
            fetchEntities();
        });
    }
}