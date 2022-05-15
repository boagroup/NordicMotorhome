package com.motorhome.controller;

import com.motorhome.FXUtils;
import com.motorhome.model.ModelInterface;
import com.motorhome.persistence.DataResult;
import com.motorhome.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    // FXML objects
    @FXML private HBox header;
    @FXML private Label usernameLabel;
    @FXML private ImageView userImage;
    @FXML private Button backButton;
    @FXML private VBox entityContainer;
    @FXML private HBox add;

    // Controller Interface composition
    private static MenuInterface<ModelInterface> menu;
    public static void setMenu(MenuInterface<ModelInterface> menu) {
        MenuController.menu = menu;
    }
    public static MenuInterface<ModelInterface> getMenu() {
        return MenuController.menu;
    }

    // Util methods
    private void createMainContainer(DataResult dataResult) {
        if (menu != null) {
            while (dataResult.next()) {
                AnchorPane inner = menu.getInnerContainer(menu.getModel(dataResult.getCurrentRow()));
                entityContainer.getChildren().add(inner);
            }
        }
    }

    private DataResult fetchData() {
        if (menu == null) {
            return null;
        }
        return Database.getInstance().executeQuery(menu.getQuery());
    }

    private void refresh() {
        entityContainer.getChildren().clear();
        createMainContainer(fetchData());
    }

    // starting point
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXUtils.setUserDetailsInHeader(usernameLabel, userImage);
        createMainContainer(fetchData());
//        String title = menu.getTitle();
        FXUtils.addBackFunctionality(backButton);

//        back.setOnAction( ae -> FXUtils.changeScene(ae, "main_menu", title,
//                        true, true, "main_menu",  -1, -1));

//        FXBuilder.addToButton(back,
//                en -> back.setStyle("-fx-background-color: red;"),
//                ex -> back.setStyle("-fx-background-color: #4bc190;"),
//                ae -> FXUtils.changeScene(ae, "main_menu", "NMH Main Menu",
//                        true, true, "main_menu",  -1, -1)
//        );
    }
}
