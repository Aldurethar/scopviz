package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.Arrays;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sun.util.logging.resources.logging;

/**
 * manages the settings of the program also stores the constants
 * 
 * @author Jascha Bohne
 * @version 1.0.0.0
 */
public final class OptionsManager {
	// SETTINGS
	/** The Default Weight for all new Edges. */
	private static int defaultWeight = 0;
	/** Flag whether to show the weight labels on Edges. */
	private static boolean showWeight = true;
	/** The default latitude of nodes (defaults to Piloty Building) */
	private static double defaultLat = 49.877559;
	/** The default longitude of nodes (defaults to Piloty Building) */
	private static double defaultLong = 8.654546;
	/** If the default coordinates have been changed */
	private static boolean coordinatesChanged = false;

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private OptionsManager() {
	}

	/**
	 * opens a dialog that can be used to edit options
	 */
	public static void openOptionsDialog() {
		// Create new Dialog
		Dialog<ArrayList<String>> addPropDialog = new Dialog<>();
		addPropDialog.setTitle("Preferences");

		ButtonType addButtonType = new ButtonType("save & exit", ButtonData.OK_DONE);
		addPropDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// create grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		// create dialog elements
		TextField defaultWeightField = new TextField(Integer.toString(defaultWeight));

		RadioButton showWeightButton = new RadioButton();
		showWeightButton.setSelected(showWeight);

		ChoiceBox<String> nodeGraphicsSelector = new ChoiceBox<String>();
		if (StylesheetManager.getAllNodeGraphics() != null) {
			ObservableList<String> styles = FXCollections
					.observableArrayList(Arrays.asList(StylesheetManager.getAllNodeGraphics()));
			nodeGraphicsSelector.setItems(FXCollections.observableArrayList(styles));
			nodeGraphicsSelector.getSelectionModel().select(StylesheetManager.getNodeGraphics());
		}

		ChoiceBox<Integer> loggingLevelSelector = new ChoiceBox<Integer>(
				FXCollections.observableArrayList(1, 2, 3));
		loggingLevelSelector.getSelectionModel().select(new Integer(Debug.getLogLevel()));
				
		TextField defaultLatitudeField = new TextField(Double.toString(defaultLat));
		TextField defaultLongitudeField = new TextField(Double.toString(defaultLong));

		// position elements on grid
		int row = 0;
		grid.add(new Label("Default weight of edges:"), 0, row);
		grid.add(defaultWeightField, 1, row);
		row++;
		grid.add(new Label("Show weight of edges in the graph viewer"), 0, row);
		grid.add(showWeightButton, 1, row);
		row++;
		if (StylesheetManager.getAllNodeGraphics().length > 1) {
			grid.add(new Label("Node display:"), 0, row);
			grid.add(nodeGraphicsSelector, 1, row);
			row++;
		}
		grid.add(new Label("Default Coordinates of Nodes without Coordinates" + (coordinatesChanged ? ":" : ".")), 0,
				row);
		grid.add(new Label(coordinatesChanged ? "" : "At the Moment set to Piloty building TU Darmstadt:"), 1, row);
		row++;
		grid.add(new Label("Latitude:"), 0, row);
		grid.add(defaultLatitudeField, 1, row);
		row++;
		grid.add(new Label("Longitude:"), 0, row);
		grid.add(defaultLongitudeField, 1, row);
		row++;
		grid.add(new Label("Logging level"), 0, row);
		grid.add(loggingLevelSelector, 1, row);
		row++;

		// set dialog
		addPropDialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> defaultWeightField.requestFocus());

		// get new property values
		addPropDialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				try {
					defaultWeight = Integer.parseInt(defaultWeightField.getText());
					if (defaultLat != Double.parseDouble(defaultLatitudeField.getText())
							|| defaultLong != Double.parseDouble(defaultLongitudeField.getText())) {
						coordinatesChanged = true;
						defaultLat = Double.parseDouble(defaultLatitudeField.getText());
						defaultLong = Double.parseDouble(defaultLongitudeField.getText());
					}
				} catch (NumberFormatException e) {
				}
				showWeight = showWeightButton.isSelected();
				StylesheetManager.adjustNodeGraphics(nodeGraphicsSelector.getValue());
				Debug.out(Debug.getLogLevel());
				Debug.setLogLevel(loggingLevelSelector.getValue());
				Debug.out(Debug.getLogLevel());
				return null;
			} else
				return null;

		});
		addPropDialog.showAndWait();

	}

	/**
	 * @return the defaultLat
	 */
	public static double getDefaultLat() {
		return defaultLat;
	}

	/**
	 * @return the defaultLong
	 */
	public static double getDefaultLong() {
		return defaultLong;
	}

	/**
	 * Returns the default weight for new Edges.
	 * 
	 * @return the default weight
	 */
	public static int getDefaultWeight() {
		return defaultWeight;
	}

	/**
	 * Sets the default weight for new Edges.
	 * 
	 * @param defaultWeight
	 *            the defaultWeight to set
	 */
	public static void setDefaultWeight(int defaultWeight) {
		OptionsManager.defaultWeight = defaultWeight;
	}

	/**
	 * Returns whether Edge weight should be displayed as labels.
	 * 
	 * @return true if weight should be shown, false otherwise
	 */
	public static boolean isWeightShown() {
		return showWeight;
	}

	/**
	 * Sets the Flag whether Edge weight should be displayed as labels.
	 * 
	 * @param showWeight
	 *            the showWeight to set
	 */
	public static void setShowWeight(boolean showWeight) {
		OptionsManager.showWeight = showWeight;
	}

}
