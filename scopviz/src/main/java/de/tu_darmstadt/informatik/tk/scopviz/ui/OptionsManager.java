package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.graphstream.ui.graphicGraph.GraphicEdge.EdgeGroup;

import com.sun.prism.paint.Color;

import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypointRenderer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.EdgePainter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.MapViewFunctions;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.WorldView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
	 * the default device size
	 */
	private static int defaultDeviceSize = 50;
	
	/**
	 * the default thickness of edges
	 */
	private static int defaultEdgeThickness = 2;
	
	/**
	 * default Color theme in symbol layer
	 */
	private static String defaultStandardEdgeColor = "Black";
	private static String defaultClickedEdgeColor = "Red";
	private static String defaultPlacementColor = "Blue";
	private static String defaultStandardDeviceColor = "Black";
	private static String defaultClickedDeviceColor = "Red";

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
		
		TextField deviceSize = new TextField(Integer.toString(CustomWaypointRenderer.getDeviceSize()));
		
		Button resetButton = new Button("Reset");
		resetButton.setOnAction((event) -> {

			edgeThickness.setText(Integer.toString(defaultEdgeThickness));
			EdgePainter.setEdgeThickness(defaultEdgeThickness);
			
			deviceSize.setText(Integer.toString(defaultDeviceSize));
			CustomWaypointRenderer.setScaleSize(defaultDeviceSize);
			
			edgeStandardColorSymbolLayer.getSelectionModel().select(defaultStandardEdgeColor);
			edgePlacementColorSymbolLayer.getSelectionModel().select(defaultPlacementColor);
			edgeSelectedColorSymbolLayer.getSelectionModel().select(defaultClickedEdgeColor);
			EdgePainter.setColor(defaultStandardEdgeColor, defaultPlacementColor, defaultClickedEdgeColor);
			
			waypointStandardColorSymbolLayer.getSelectionModel().select(defaultStandardDeviceColor);
			waypointSelectedColorSymbolLayer.getSelectionModel().select(defaultClickedDeviceColor);
			CustomWaypointRenderer.setColor(defaultStandardDeviceColor, defaultClickedDeviceColor);
			
		});

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
		
		// symbol layer stuff
		grid.add(new Label(""), 1, row); row++;
		grid.add(new Label("Symbol-Layer Options"), 1, row); row++;
		grid.add(new Label("Device Size (int):"), 0, row);
		grid.add(deviceSize, 1, row); row++;
		
		grid.add(new Label("Edge thickness (int):"), 0, row);
		grid.add(edgeThickness, 1, row); row++;
		
		grid.add(new Label(""), 1, row); row++;
		grid.add(new Label("Edge Colors"), 1, row); row++;
		grid.add(new Label("Standard Edge Color"), 0, row);
		grid.add(edgeStandardColorSymbolLayer, 1, row); row++;
		
		grid.add(new Label("Clicked Edge Color"), 0, row);
		grid.add(edgeSelectedColorSymbolLayer, 1, row); row++;
		
		grid.add(new Label("Placement Edge Color"), 0, row);
		grid.add(edgePlacementColorSymbolLayer, 1, row); row++;
		
		grid.add(new Label("Device Colors"), 1, row); row++;
		grid.add(new Label("Standard Device Color"), 0, row);
		grid.add(waypointStandardColorSymbolLayer, 1, row); row++;
		
		grid.add(new Label("Clicked Device Color"), 0, row);
		grid.add(waypointSelectedColorSymbolLayer, 1, row); row++;
		
		grid.add(new Label(""), 1, row); row++;
		grid.add(resetButton, 1, row);

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
					if(Integer.parseInt(deviceSize.getText()) != CustomWaypointRenderer.getDeviceSize()) {
						CustomWaypointRenderer.setScaleSize(Integer.parseInt(deviceSize.getText()));
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
