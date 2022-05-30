package com.motorhome.controller.main;

import com.motorhome.utilities.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Abstract class to take advantage of the fact that all menus except main are very similar.
 * Author(s): Bartosz Birylo
 */
public abstract class MenuController implements Initializable {
	@FXML protected Label usernameLabel;
	@FXML protected ImageView userImage;
	@FXML protected Button backButton;
	@FXML protected VBox entityContainer;
	@FXML protected Label entityCountLabel;
	@FXML protected HBox add;

	// String gets changed depending on what order was last used.
	// Can be used to flip order, not using boolean for clarity.
	protected String currentOrder;

	/**
	 *Flips the order of the entities in the Scene.
	 * @param field Field that is selected to flip the order (e.g. "start date")
	 */
	protected final void flipOrder(String field) {
		if (currentOrder.equals("ASC")) {
			fetchEntities(field, "DESC");
		} else fetchEntities(field, "ASC");
	}

	protected final void prepare() {
		FXUtils.setUserDetailsInHeader(usernameLabel, userImage);
		fetchEntities();
		prepareToolbar();
		backButton.setOnAction(actionEvent -> FXUtils.changeRoot( "main_menu", "main_menu", backButton));
	}


	public abstract void fetchEntities();
	public abstract void fetchEntities(String column, String order);
	protected abstract void prepareToolbar();
	@Override public abstract void initialize(URL url, ResourceBundle resourceBundle);
}
