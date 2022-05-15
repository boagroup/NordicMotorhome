package com.motorhome.controller.Entity;

import com.motorhome.model.ModelInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public interface EntityInterface <T extends ModelInterface>{

	ImageView getImageView();

	HBox getLabelContainer();

	// get event of what will happen on edit
	EventHandler<ActionEvent> getEditEvent();

	// get event of what will happen on remove
	EventHandler<ActionEvent> getRemoveEvent();
}
