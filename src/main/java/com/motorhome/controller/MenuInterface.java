package com.motorhome.controller;

import com.motorhome.model.ModelInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

	// Creates an inner AnchorPane that will be inserted into Vbox container
	AnchorPane getInnerContainer(T entity);

	// get event of what will happen on Add
	EventHandler<ActionEvent> getAddEvent();

	// get event of what will happen on edit
	EventHandler<ActionEvent> getEditEvent();

	// get event of what will happen on remove
	EventHandler<ActionEvent> getRemoveEvent();
}
