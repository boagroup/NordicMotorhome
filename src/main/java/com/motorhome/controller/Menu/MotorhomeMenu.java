package com.motorhome.controller.Menu;

import com.motorhome.FXTools.FXUtils;
import com.motorhome.controller.Entity.EntityInterface;
import com.motorhome.controller.EntityController;
import com.motorhome.model.ModelInterface;
import com.motorhome.model.Motorhome;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class MotorhomeMenu implements MenuInterface<Motorhome> {
    @Override
    public String getQuery() {
        return "SELECT * FROM motorhomes;";
    }

    @Override
    public String getTitle() {
        return "Motorhome Menu";
    }

    @Override
    public Motorhome getModel(Map<String, Object> row) {
        return new Motorhome(row);
    }

    @Override
    public Node getEntity(Motorhome model) {
        int id = model.getId();
        int beds = model.getBeds();
        String image = model.getImage();
        String type = model.getType();
        EntityController.setEntity(new EntityInterface<ModelInterface>() {
            @Override
            public ImageView getImageView() {
                ImageView img = new ImageView();
                Image i = new Image(Objects.requireNonNull(getClass().getResource(image)).toExternalForm());
                img.setImage(i);
                return img;
            }

            @Override
            public HBox getLabelContainer() {
                return null;
            }

            @Override
            public EventHandler<ActionEvent> getEditEvent() {
                return event -> {

                };
            }

            @Override
            public EventHandler<ActionEvent> getRemoveEvent() {
                return event -> {

                };
            }
        });

        Parent root = null;
        try {
            new FXMLLoader();
            root = FXMLLoader.load(Objects.requireNonNull(FXUtils.class.getResource("/view/entity.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        EntityController.setEntity(null);
        return root;
    }

    @Override
    public EventHandler<ActionEvent> getAddEvent() {
        return event -> {

        };
    }
}