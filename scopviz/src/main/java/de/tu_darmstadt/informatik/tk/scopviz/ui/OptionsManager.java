package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;

import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypointRenderer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.EdgePainter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.MapViewFunctions;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.WorldView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
		nodeGraphicsSelector.setItems(FXCollections.observableArrayList(StylesheetManager.getAllNodeGraphics()[0],
				StylesheetManager.getAllNodeGraphics()[1]));
		nodeGraphicsSelector.getSelectionModel().select(StylesheetManager.getNodeGraphics());

		TextField defaultLatitudeField = new TextField(Double.toString(defaultLat));
		TextField defaultLongitudeField = new TextField(Double.toString(defaultLong));
		
		
		// Symbol Layer options
		ChoiceBox<String> edgeSelectedColorSymbolLayer = new ChoiceBox<String>();
		edgeSelectedColorSymbolLayer.setItems(FXCollections.observableArrayList("Red", "Black", "Blue", "Green", "Yellow", "Orange", "Gray"));
		edgeSelectedColorSymbolLayer.getSelectionModel().select(EdgePainter.getClickedColor());
		
		ChoiceBox<String> edgePlacementColorSymbolLayer = new ChoiceBox<String>();
		edgePlacementColorSymbolLayer.setItems(FXCollections.observableArrayList("Blue", "Black", "Red", "Green", "Yellow", "Orange", "Gray"));
		edgePlacementColorSymbolLayer.getSelectionModel().select(EdgePainter.getPlacementColor());
		
		ChoiceBox<String> edgeStandardColorSymbolLayer = new ChoiceBox<String>();
		edgeStandardColorSymbolLayer.setItems(FXCollections.observableArrayList("Black", "Red", "Blue", "Green", "Yellow", "Orange", "Gray"));
		edgeStandardColorSymbolLayer.getSelectionModel().select(EdgePainter.getStandardColor());
		
		ChoiceBox<String> waypointStandardColorSymbolLayer = new ChoiceBox<String>();
		waypointStandardColorSymbolLayer.setItems(FXCollections.observableArrayList("Black", "Red", "Blue", "Green", "Yellow", "Orange", "Gray"));
		waypointStandardColorSymbolLayer.getSelectionModel().select(CustomWaypointRenderer.getStandardColor());
		
		ChoiceBox<String> waypointSelectedColorSymbolLayer = new ChoiceBox<String>();
		waypointSelectedColorSymbolLayer.setItems(FXCollections.observableArrayList("Red", "Black", "Blue", "Green", "Yellow", "Orange", "Gray"));
		waypointSelectedColorSymbolLayer.getSelectionModel().select(CustomWaypointRenderer.getClickedColor());
		
		TextField edgeThickness = new TextField(Integer.toString(EdgePainter.getThickness()));
		
		TextField waypointSize = new TextField(Integer.toString(CustomWaypointRenderer.getWaypointSize()));

		// position elements on grid
		grid.add(new Label("Default weight of edges:"), 0, 0);
		grid.add(defaultWeightField, 1, 0);
		grid.add(new Label("Show weight of edges in the graph viewer"), 0, 1);
		grid.add(showWeightButton, 1, 1);
		grid.add(new Label("Node display:"), 0, 2);
		grid.add(nodeGraphicsSelector, 1, 2);
		grid.add(new Label("Default Coordinates of Nodes without Coordinates" + (coordinatesChanged ? ":" : ".")), 0,
				3);
		grid.add(new Label(coordinatesChanged ? "" : "At the Moment set to Piloty building TU Darmstadt:"), 1, 3);
		grid.add(new Label("Latitude:"), 0, 4);
		grid.add(defaultLatitudeField, 1, 4);
		grid.add(new Label("Longitude:"), 0, 5);
		grid.add(defaultLongitudeField, 1, 5);
		
		// symbol layer stuff
		grid.add(new Label(""), 1, 6);
		grid.add(new Label("Symbol-Layer Options"), 1, 7);
		grid.add(new Label("Waypoint Size (int):"), 0, 8);
		grid.add(waypointSize, 1, 8);
		
		grid.add(new Label("Edge thickness (int):"), 0, 9);
		grid.add(edgeThickness, 1, 9);
		
		grid.add(new Label("Edge Colors"), 1, 10);
		grid.add(new Label("Standard Edge Color"), 0, 11);
		grid.add(edgeStandardColorSymbolLayer, 1, 11);
		
		grid.add(new Label("Clicked Edge Color"), 0, 12);
		grid.add(edgeSelectedColorSymbolLayer, 1, 12);
		
		grid.add(new Label("Placement Edge Color"), 0, 13);
		grid.add(edgePlacementColorSymbolLayer, 1, 13);
		
		grid.add(new Label("Waypoint Colors"), 1, 14);
		grid.add(new Label("Standard Waypoint Color"), 0, 15);
		grid.add(waypointStandardColorSymbolLayer, 1, 15);
		
		grid.add(new Label("Clicked Waypoint Color"), 0, 16);
		grid.add(waypointSelectedColorSymbolLayer, 1, 16);

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
					
					// symbol layer edge thickness
					if(Integer.parseInt(edgeThickness.getText()) != EdgePainter.getThickness()) {
						EdgePainter.setEdgeThickness(Integer.parseInt(edgeThickness.getText()));
					}
					// symbol layer waypoint size
					if(Integer.parseInt(waypointSize.getText()) != CustomWaypointRenderer.getWaypointSize()) {
						CustomWaypointRenderer.setScaleSize(Integer.parseInt(waypointSize.getText()));
						MapViewFunctions.resetImageMap();
						MapViewFunctions.initializeWaypointImages();
					}
					
				} catch (NumberFormatException e) {
				}
				showWeight = showWeightButton.isSelected();
				StylesheetManager.adjustNodeGraphics(nodeGraphicsSelector.getValue());
				
				// color types of waypoints and edges
				EdgePainter.setColor(edgeStandardColorSymbolLayer.getValue(), edgePlacementColorSymbolLayer.getValue(), edgeSelectedColorSymbolLayer.getValue());
				CustomWaypointRenderer.setColor(waypointStandardColorSymbolLayer.getValue(), waypointSelectedColorSymbolLayer.getValue());
				
				WorldView.internMapViewer.repaint();
				
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
