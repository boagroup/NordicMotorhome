package com.motorhome.FXTools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FXBuilder {
	private FXBuilder(){}

	public record Dimensions(double height, double weight) {}

	public static String defContainerStyle = "-fx-border-style: solid; -fx-border-color:#b3b3b3;";
	public static String defButtonStyle = "-fx-background-color: #7be3ad;";

	public static Dimensions defContainerSize = new Dimensions(100, 1280);
	public static Dimensions defImageSize = new Dimensions(64,64);
	public static Dimensions defButtonSize = new Dimensions(54,0);

	public static Font defFont = Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 15);


	public static AnchorPane createContainer(Dimensions prefDimensions, Dimensions minDimensions, String style) {
		// Setting up HBox container for child instance
		AnchorPane innerContainer = new AnchorPane();
		innerContainer.setPrefSize(prefDimensions.height, prefDimensions.weight);
		innerContainer.setMinSize(minDimensions.height, minDimensions.height);
		innerContainer.setStyle(style);
		return innerContainer;
	}

	public static ImageView createImageView(Dimensions imgDimensions, boolean pickOnBounds, boolean preserveRatio, Image image) {
		ImageView img = new ImageView();
		img.setFitHeight(imgDimensions.height);
		img.setFitWidth(imgDimensions.weight);
		img.setPickOnBounds(pickOnBounds);
		img.setPreserveRatio(preserveRatio);
		img.setImage(image);
		return img;
	}

	public static Label createLabel(String text, Font font) {
		// Setting up name label
		Label label = new Label();
		label.setText(text);
		label.setFont(font);
		return label;
	}

	public static Button createButton(String text, Dimensions dimensions, String style, boolean mnemonicParsing) {
		Button button = new Button();
		button.setText(text);
		button.setMinSize(dimensions.height, dimensions.weight);
		button.setStyle(style);
		button.setMnemonicParsing(mnemonicParsing);
		return button;
	}

	public static Button addToButton(Button button, EventHandler<MouseEvent> eventOnMouseEntered,
	                                 EventHandler<MouseEvent> eventOnMouseExited, EventHandler<ActionEvent> action) {
		button.setOnMouseEntered(eventOnMouseEntered);
		button.setOnMouseExited(eventOnMouseExited);
		button.setOnAction(action);
		return button;
	}

	public static void setLayout(Node node, Dimensions dimensions){
		node.setLayoutX(dimensions.weight);
		node.setLayoutY(dimensions.height);
	}

	public static AnchorPane addToContainer(AnchorPane innerContainer, Node... children) {
		for (Node child: children) {
			innerContainer.getChildren().add(child);
		}
		return innerContainer;
	}
}

