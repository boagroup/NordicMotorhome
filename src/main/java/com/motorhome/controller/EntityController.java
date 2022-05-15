package com.motorhome.controller;

import com.motorhome.controller.Entity.EntityInterface;
import com.motorhome.model.ModelInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EntityController implements Initializable {

	private static EntityInterface<ModelInterface> entity;
	public static void setEntity(EntityInterface<ModelInterface> entity) {
		EntityController.entity = entity;
	}

	@FXML private ImageView image;
	@FXML private HBox labelContainer;
	@FXML private MenuItem edit;
	@FXML private MenuItem delete;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		image = entity.getImageView();
		labelContainer = entity.getLabelContainer();
		edit.setOnAction(entity.getEditEvent());
		delete.setOnAction(entity.getRemoveEvent());
	}

}
