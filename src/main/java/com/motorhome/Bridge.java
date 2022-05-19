package com.motorhome;

import com.motorhome.controller.main.MainMenuController;
import com.motorhome.controller.motorhome.MotorhomeMenuController;
import com.motorhome.controller.motorhome.MotorhomeSettingsController;
import com.motorhome.controller.staff.StaffMenuController;

/**
 * "Bridges" the gap between Controllers and other classes.
 * As JavaFX nodes are dynamic private attributes, they cannot be directly accessed outside their class.
 * Using Getters directly cannot be done either.
 * Author(s): Octavian Roman
 */
public final class Bridge {
    private static MainMenuController mainMenuController = null;
    private static StaffMenuController staffMenuController = null;
    private static MotorhomeMenuController motorhomeMenuController = null;
    private static MotorhomeSettingsController motorhomeSettingsController = null;

    private Bridge() {}

    // Getters

    public static MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public static StaffMenuController getStaffMenuController() {
        return staffMenuController;
    }

    public static MotorhomeMenuController getMotorhomeMenuController() {
        return motorhomeMenuController;
    }

    public static MotorhomeSettingsController getMotorhomeSettingsController() {
        return motorhomeSettingsController;
    }

    // Setters

    public static void setMainMenuController(MainMenuController mainMenuController) {
        Bridge.mainMenuController = mainMenuController;
    }

    public static void setStaffMenuController(StaffMenuController staffMenuController) {
        Bridge.staffMenuController = staffMenuController;
    }

    public static void setMotorhomeMenuController(MotorhomeMenuController motorhomeMenuController) {
        Bridge.motorhomeMenuController = motorhomeMenuController;
    }

    public static void setMotorhomeSettingsController(MotorhomeSettingsController motorhomeSettingsController) {
        Bridge.motorhomeSettingsController = motorhomeSettingsController;
    }
}