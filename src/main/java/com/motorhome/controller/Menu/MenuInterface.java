package com.motorhome.controller.Menu;

import com.motorhome.controller.Entity.EntityInterface;
import com.motorhome.controller.EntityController;
import com.motorhome.model.ModelInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

public interface MenuInterface<T extends ModelInterface> {
	// get Database Query
	String getQuery();

	// get title of scene
	String getTitle();

	// Returns a row with table names and its' contents
	// used to create ModelInterface instances
	T getModel(Map<String, Object> row);

	// Creates an inner entity that will be inserted into Vbox container
	Node getEntity(T model);

	// get event of what will happen on Add
	EventHandler<ActionEvent> getAddEvent();

}
