package com.motorhome.utilities;

import com.motorhome.controller.main.MainMenuController;
import com.motorhome.controller.motorhome.MotorhomeMenuController;
import com.motorhome.controller.motorhome.popup.MotorhomeSettingsController;
import com.motorhome.controller.rental.*;
import com.motorhome.controller.rental.entity.ExtraSelectionEntityController;
import com.motorhome.controller.rental.popup.RentalAddController;
import com.motorhome.controller.rental.popup.RentalEditController;
import com.motorhome.controller.rental.popup.RentalMotorhomeSelectionController;
import com.motorhome.controller.rental.popup.RentalSettingsController;
import com.motorhome.controller.staff.StaffMenuController;

/**
 * "Bridges" the gap between Controllers and other classes.
 * As JavaFX nodes are dynamic private attributes, they cannot be directly accessed outside their class.
 * Using Getters directly cannot be done either.
 * Author(s): Octavian Roman
 */
public final class Bridge {
    private static StaffMenuController staffMenuController = null;
    private static MotorhomeMenuController motorhomeMenuController = null;
    private static MotorhomeSettingsController motorhomeSettingsController = null;
    private static RentalMenuController rentalMenuController = null;
    private static RentalSettingsController rentalSettingsController = null;
    private static RentalAddController rentalAddController = null;
    private static RentalMotorhomeSelectionController rentalMotorhomeSelectionController = null;
    private static RentalEditController rentalEditController = null;

    private Bridge() {}

    // Getters
    public static StaffMenuController getStaffMenuController() {
        return staffMenuController;
    }

    public static MotorhomeMenuController getMotorhomeMenuController() {
        return motorhomeMenuController;
    }

    public static MotorhomeSettingsController getMotorhomeSettingsController() {
        return motorhomeSettingsController;
    }

    public static RentalMenuController getRentalMenuController() {
        return rentalMenuController;
    }

    public static RentalSettingsController getRentalSettingsController() {
        return rentalSettingsController;
    }

    public static RentalAddController getRentalAddController() {
        return rentalAddController;
    }

    public static RentalMotorhomeSelectionController getRentalMotorhomeSelectionController() {
        return rentalMotorhomeSelectionController;
    }

    public static RentalEditController getRentalEditController() {
        return rentalEditController;
    }

    // Setters
    public static void setStaffMenuController(StaffMenuController staffMenuController) {
        Bridge.staffMenuController = staffMenuController;
    }

    public static void setMotorhomeMenuController(MotorhomeMenuController motorhomeMenuController) {
        Bridge.motorhomeMenuController = motorhomeMenuController;
    }

    public static void setMotorhomeSettingsController(MotorhomeSettingsController motorhomeSettingsController) {
        Bridge.motorhomeSettingsController = motorhomeSettingsController;
    }

    public static void setRentalMenuController(RentalMenuController rentalMenuController) {
        Bridge.rentalMenuController = rentalMenuController;
    }

    public static void setRentalSettingsController(RentalSettingsController rentalSettingsController) {
        Bridge.rentalSettingsController = rentalSettingsController;
    }

    public static void setRentalAddController(RentalAddController rentalAddController) {
        Bridge.rentalAddController = rentalAddController;
    }

    public static void setRentalMotorhomeSelectionController(RentalMotorhomeSelectionController rentalMotorhomeSelectionController) {
        Bridge.rentalMotorhomeSelectionController = rentalMotorhomeSelectionController;
    }

    public static void setRentalEditController(RentalEditController rentalEditController) {
        Bridge.rentalEditController = rentalEditController;
    }
}