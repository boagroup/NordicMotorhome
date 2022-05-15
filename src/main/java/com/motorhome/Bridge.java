package com.motorhome;

import com.motorhome.controller.MainMenuController;
import com.motorhome.controller.Menu.StaffMenuController;

/**
 * "Bridges" the gap between Controllers and other classes.
 * As JavaFX nodes are dynamic private attributes, they cannot be directly accessed outside their class.
 * Using Getters directly cannot be done either.
 * Author(s): Octavian Roman
 */
public final class Bridge {
    private static MainMenuController mainMenuController = null;
    private static StaffMenuController staffMenuController = null;

    private Bridge() {}

    // Getters

    public static MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public static StaffMenuController getStaffMenuController() {
        return staffMenuController;
    }

    // Setters

    public static void setMainMenuController(MainMenuController mainMenuController) {
        Bridge.mainMenuController = mainMenuController;
    }

    public static void setStaffMenuController(StaffMenuController staffMenuController) {
        Bridge.staffMenuController = staffMenuController;
    }
}
