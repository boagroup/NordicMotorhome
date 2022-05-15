package com.motorhome.controller.Menu;

import com.motorhome.controller.MenuInterface;
import com.motorhome.model.ModelInterface;
import com.motorhome.model.Motorhome;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.util.Map;

public class MotorhomeMenu implements MenuInterface<Motorhome> {
    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Motorhome getModel(Map<String, Object> row) {
        return null;
    }

    @Override
    public AnchorPane getInnerContainer(Motorhome entity) {
        return null;
    }

    @Override
    public EventHandler<ActionEvent> getAddEvent() {
        return null;
    }

    @Override
    public EventHandler<ActionEvent> getEditEvent() {
        return null;
    }

    @Override
    public EventHandler<ActionEvent> getRemoveEvent() {
        return null;
    }
}