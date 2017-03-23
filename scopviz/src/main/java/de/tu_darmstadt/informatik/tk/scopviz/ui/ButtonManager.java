package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.graph.implementations.Graphs;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypoint;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypointRenderer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.MapViewFunctions;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.WorldView;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de), Julian Ohl, Dominik
 *         Renkel
 * @version 1.2
 *
 */
public final class ButtonManager {

	/** List of the Buttons for Layer switching. */
	private static ArrayList<Button> layerButtons;

	/**
	 * Reference to the GUI Controller for Access to various GUI Elements.
	 */
	private static GUIController controller;

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private ButtonManager() {
	}

	/**
	 * Initializes the ButtonManager with a List of Buttons for Layer switching.
	 * 
	 * @param nList
	 *            the Layer switching Buttons
	 * @param guiController
	 *            a Reference to the GUI Controller for Access
	 * @param uButton
	 *            a Reference to the Underlay switch Button for marking it as
	 *            active on startup
	 * 
	 */
	public static void initialize(ArrayList<Button> nList, GUIController guiController, Button uButton) {
		layerButtons = nList;

		controller = guiController;
		setBorderStyle(uButton);
	}

	/**
	 * Handler for zoom in Button.
	 */
	public static void zoomInAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			WorldView.internMapViewer.setZoom(WorldView.internMapViewer.getZoom() - 1);
		} else {
			Main.getInstance().getGraphManager().zoomIn();
		}
	}

	/**
	 * Handler for zoom out Button.
	 */
	public static void zoomOutAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			WorldView.internMapViewer.setZoom(WorldView.internMapViewer.getZoom() + 1);
		} else {
			Main.getInstance().getGraphManager().zoomOut();
		}
	}

	/**
	 * After switching from symbol-layer to other layer show toolbox and make
	 * properties editable again.
	 */
	private static void switchfromSymbolLayer() {

		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {

			// show toolbox and hide VBox
			controller.toolbox.setVisible(true);

			controller.topLeftAPane.getChildren().remove(controller.symbolToolVBox);

			controller.symbolToolVBox.setVisible(false);

			// make properties editable again
			controller.propertiesObjectColumn.setEditable(true);

			// enabel context menu
			controller.properties.setRowFactory(PropertiesManager.rightClickCallback);

			// show graph instead of map view
			controller.swingNodeWorldView.setVisible(false);
			controller.swingNode.setVisible(true);

			// make map view mouse transparent
			controller.stackPane.setMouseTransparent(true);
			controller.swingNodeWorldView.setMouseTransparent(true);

			// make graph non mouse transparent
			controller.pane.setMouseTransparent(false);
			controller.swingNode.setMouseTransparent(false);

			// deselect graph element
			PropertiesManager.showNewDataSet(null);

			// reset loaded images
			MapViewFunctions.resetImageMap();

		}
	}

	/**
	 * Handler for the Underlay Layer switch Button.
	 */
	public static void underlayAction(ActionEvent arg0) {
		Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
		
		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
		GraphDisplayManager.switchActiveGraph();

		ToolboxManager.setUnderlayItems();
		setBorderStyle((Button) arg0.getSource());

		controller.open.setText("Open...");

		controller.newItem.disableProperty().set(false);
		controller.open.disableProperty().set(false);
		controller.add.disableProperty().set(true);
		controller.save.disableProperty().set(false);
		controller.saveAs.disableProperty().set(false);
		controller.delete.disableProperty().set(false);
		controller.undelete.disableProperty().set(false);
		controller.updateMetricMI.disableProperty().set(true);

		// hide metricbox/update button
		controller.rightSide.getChildren().remove(controller.updateButtonAPane);
		controller.metricbox.setVisible(false);
	}

	/**
	 * Handler for the Operator Layer switch Button.
	 */
	public static void operatorAction(ActionEvent arg0) {
		Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
		
		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
		GraphDisplayManager.switchActiveGraph();

		ToolboxManager.setOperatorItems();
		setBorderStyle((Button) arg0.getSource());

		controller.open.setText("Open...");

		controller.newItem.disableProperty().set(false);
		controller.open.disableProperty().set(false);
		controller.add.disableProperty().set(false);
		controller.save.disableProperty().set(false);
		controller.saveAs.disableProperty().set(false);
		controller.delete.disableProperty().set(false);
		controller.undelete.disableProperty().set(false);
		controller.updateMetricMI.disableProperty().set(true);

		// hide metricbox/update button
		controller.rightSide.getChildren().remove(controller.updateButtonAPane);
		controller.metricbox.setVisible(false);
	}

	/**
	 * Handler for the Mapping Layer switch Button.
	 */
	public static void mappingAction(ActionEvent arg0) {
		Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
		
		// show metricbox/update button
		if (!(GraphDisplayManager.getCurrentLayer().equals(Layer.MAPPING))) {
			controller.rightSide.getChildren().add(controller.updateButtonAPane);
			controller.metricbox.setVisible(true);
		}

		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
		GraphDisplayManager.switchActiveGraph();

		ToolboxManager.setMappingItems();
		setBorderStyle((Button) arg0.getSource());

		controller.open.setText("Open Mapping...");

		controller.newItem.disableProperty().set(true);
		controller.open.disableProperty().set(false);
		controller.add.disableProperty().set(true);
		controller.save.disableProperty().set(false);
		controller.saveAs.disableProperty().set(false);
		controller.delete.disableProperty().set(false);
		controller.undelete.disableProperty().set(false);
		controller.updateMetricMI.disableProperty().set(false);

	}

	/**
	 * Handler for the Symbol Representation Layer switch Button.
	 */
	public static void symbolRepAction(ActionEvent arg0) {
		Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
		
		if (!(GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL))) {

			// add a copy of the underlay graph to the the symbol layer
			MyGraph gClone = (MyGraph) Graphs.clone(GraphDisplayManager.getGraphManager(Layer.UNDERLAY).getGraph());
			gClone.removeAttribute("layer");
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.addGraph(gClone, true);
			controller.topLeftAPane.getChildren().add(controller.symbolToolVBox);
		}

		try {
			// load world view
			activateWorldView();

		} catch (IOException e) {

			// problems with Internet connection, maybe host not reachable,
			// maybe no Internet connection at all
			GraphDisplayManager.switchActiveGraph();
			setBorderStyle((Button) arg0.getSource());

			// show "Connection Error" message
			showConnectionErrorMsg();

			return;
		}

		// hide metricbox/update button
		controller.rightSide.getChildren().remove(controller.updateButtonAPane);
		controller.metricbox.setVisible(false);

		GraphDisplayManager.switchActiveGraph();
		setBorderStyle((Button) arg0.getSource());

		controller.open.setText("Open...");

		controller.newItem.disableProperty().set(true);
		controller.open.disableProperty().set(true);
		controller.add.disableProperty().set(true);
		controller.save.disableProperty().set(true);
		controller.saveAs.disableProperty().set(true);
		controller.delete.disableProperty().set(true);
		controller.undelete.disableProperty().set(true);
		controller.updateMetricMI.disableProperty().set(true);

	}

	/**
	 * show an Alert dialog when OpenStreetMap could not be loaded
	 */
	public static void showConnectionErrorMsg() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Connection Error");
		alert.setHeaderText("Could not reach OpenStreetMap server");
		alert.setContentText(null);

		alert.showAndWait();
	}

	/**
	 * Initializes the WorldView, sets data and paints them.
	 * 
	 * @throws IOException
	 */
	private static void activateWorldView() throws IOException {

		// dont show graph and toolbox
		controller.toolbox.setVisible(false);
		controller.swingNode.setVisible(false);

		// make properties uneditable
		controller.propertiesObjectColumn.setEditable(false);

		// make map view non mouse transparent
		controller.stackPane.setMouseTransparent(false);
		controller.swingNodeWorldView.setMouseTransparent(false);

		// make graph mouse transparent
		controller.pane.setMouseTransparent(true);
		controller.swingNode.setMouseTransparent(true);

		// show VBox for map options
		controller.symbolToolVBox.setVisible(true);

		WorldView.loadWorldView();

		MapViewFunctions.checkVBoxChanged();

		WorldView.internMapViewer.repaint();

		// set content to UI Element
		controller.swingNodeWorldView.setContent(WorldView.internMapViewer);
		controller.swingNodeWorldView.setVisible(true);
	}

	/**
	 * Functionality for "edge visible" Checkbox.
	 * 
	 * @param ov
	 * @param oldVal
	 *            Checkbox previous state (Checked or unchecked)
	 * @param newVal
	 *            Checkbox current state (Checked or unchecked)
	 */
	public static void edgeVisibilitySwitcher(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
		// Show edges
		if (newVal) {
			WorldView.edgePainter.setShowEdges(true);
			WorldView.internMapViewer.repaint();
		} else {
			// Hide edges
			WorldView.edgePainter.setShowEdges(false);
			WorldView.internMapViewer.repaint();
		}
	}

	/**
	 * Functionality for "label visible" Checkbox.
	 * 
	 * @param ov
	 * @param oldVal
	 *            Checkbox previous state (Checked or unchecked)
	 * @param newVal
	 *            Checkbox current state (Checked or unchecked)
	 */
	public static void labelVisibilitySwitcher(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {

		WaypointPainter<CustomWaypoint> waypointPainter = WorldView.waypointPainter;
		CustomWaypointRenderer renderer = new CustomWaypointRenderer();

		if (newVal) {
			// Show node labels
			renderer.setShowLabels(true);
			waypointPainter.clearCache();
			waypointPainter.setRenderer(renderer);
			WorldView.internMapViewer.repaint();

		} else {
			// Hide node labels
			renderer.setShowLabels(false);
			waypointPainter.clearCache();
			waypointPainter.setRenderer(renderer);
			WorldView.internMapViewer.repaint();
		}
	}

	/**
	 * Functionality for "edge weights visible" Checkbox.
	 * 
	 * @param ov
	 * @param oldVal
	 *            Checkbox previous state (Checked or unchecked)
	 * @param newVal
	 *            Checkbox current state (Checked or unchecked)
	 */
	public static void edgeWeightVisibilitySwitcher(ObservableValue<? extends Boolean> ov, Boolean oldVal,
			Boolean newVal) {

		if (newVal) {
			// Show Edges weights
			WorldView.edgePainter.setShowWeights(true);
			WorldView.internMapViewer.repaint();

		} else {
			// Hide Edges weights
			WorldView.edgePainter.setShowWeights(false);
			WorldView.internMapViewer.repaint();
		}
	}

	/**
	 * Changes the border of the button that was pressed to red.
	 * 
	 * @param currentButton
	 *            the button that was pressed
	 */
	private static void setBorderStyle(Button currentButton) {

		for (Button j : layerButtons) {
			if (j.equals(currentButton)) {
				j.setStyle(
						"-fx-background-color: #039ED3, #039ED3, #039ED3, -fx-faint-focus-color, -fx-body-color; -fx-background-insets: -0.2, 1, 2, -1.4, 2.6; -fx-background-radius: 3, 2, 1, 4, 1;");
			} else {
				j.setStyle("-fx-border-width: 0;");
			}

		}
	}

	/**
	 * update mapViewer if choiceBox item was changed.
	 * 
	 * @param ov
	 *            The observed Value
	 * @param oldVal
	 *            Its old Value
	 * @param newVal
	 *            Its new Value
	 */
	public static void mapViewChoiceChange(ObservableValue<? extends String> ov, String oldVal, String newVal) {
		MapViewFunctions.changeMapView();
	}

}
