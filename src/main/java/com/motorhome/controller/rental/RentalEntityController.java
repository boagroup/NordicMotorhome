package com.motorhome.controller.rental;

import com.motorhome.FXUtils;
import com.motorhome.model.*;
import com.motorhome.persistence.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class RentalEntityController implements Initializable {

    // This class is always going to insert the last entity inside the ArrayList.
    // The same index for brands and models since they get added simultaneously.
    public final int entityIndex = Session.rentalEntityList.size() - 1;

    @FXML private ImageView image;
    @FXML private Label motorhomeLabel;
    @FXML private Label clientLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label priceLabel;
    @FXML private MenuItem edit;
    @FXML private MenuItem delete;

/*    private double calculatePrice(Rental rental, Brand brand, Model model, ArrayList<Extra> extraArrayList) {
        double conjointPriceOfExtras = 0.00;
        for (Extra extra : extraArrayList) {
            conjointPriceOfExtras += extra.getPrice();
        }
        double dailyPrice = 0.00;
        switch (rental.getSeason()) {
            case "L" -> dailyPrice = brand.getPrice() + model.getPrice() + conjointPriceOfExtras;
            case "M" -> dailyPrice = (brand.getPrice() + model.getPrice() + conjointPriceOfExtras) * 1.30;
            case "H" -> dailyPrice = (brand.getPrice() + model.getPrice() + conjointPriceOfExtras) * 1.60;
        }
        return dailyPrice;
    }
 */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rental rental = Session.rentalEntityList.get(entityIndex);
        Client client = Session.clientEntityList.get(entityIndex);
        Motorhome motorhome = Session.motorhomeEntityList.get(entityIndex);
        Brand brand = Session.brandEntityList.get(entityIndex);
        Model model = Session.modelEntityList.get(entityIndex);
        ArrayList<Extra> extraArrayList = Session.rentalExtrasCollectionList.get(entityIndex);

        Image i = new Image(Objects.requireNonNullElse(getClass().getResource(motorhome.getImage()),
                getClass().getResource("/assets/motorhomes/motorhome_placeholder.png")).toExternalForm());
        image.setImage(i);

        motorhomeLabel.setText(brand.getName() + " " + model.getName());
        clientLabel.setText(client.getFirstName() + " " + client.getLastName());
        startDateLabel.setText(String.valueOf(rental.getStart_date()));
        endDateLabel.setText(String.valueOf(rental.getEnd_date()));
        priceLabel.setText(FXUtils.formatCurrencyValues(rental.getFinal_price()));

    }
}
