package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;

import org.graphstream.graph.implementations.Graphs;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypoint;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypointRenderer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.MapViewFunctions;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.WorldView;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
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
	public static final void zoomInAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			WorldView.internMapViewer.setZoom(WorldView.internMapViewer.getZoom() - 1);
		} else {
			Main.getInstance().getGraphManager().zoomIn();
		}
	}

	/**
	 * Handler for zoom out Button.
	 */
	public static final void zoomOutAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			WorldView.internMapViewer.setZoom(WorldView.internMapViewer.getZoom() + 1);
		} else {
			Main.getInstance().getGraphManager().zoomOut();
		}
	}

	/**
	 * After switching from symbol-layer to other layer show toolbox and make
	 * properties editable again
	 */
	private static void switchfromSymbolLayer() {

		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {

			// show toolbox and hide VBox
			controller.toolbox.setVisible(true);
			controller.symbolToolVBox.setVisible(false);

			// make properties editable again
			controller.propertiesObjectColumn.setEditable(true);

			// show graph instead of map view
			controller.swingNodeWorldView.setVisible(false);
			controller.swingNode.setVisible(true);

			// make map view mouse transparent
			controller.stackPane.setMouseTransparent(true);
			controller.swingNodeWorldView.setMouseTransparent(true);

			// make graph non mouse transparent
			controller.pane.setMouseTransparent(false);
			controller.swingNode.setMouseTransparent(false);

		}
	}

	/**
	 * Handler for the Underlay Layer switch Button.
	 */
	public static final void underlayAction(ActionEvent arg0) {

		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
		GraphDisplayManager.switchActiveGraph();

		setBorderStyle((Button) arg0.getSource());

	}

	/**
	 * Handler for the Operator Layer switch Button.
	 */
	public static final void operatorAction(ActionEvent arg0) {

		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
		GraphDisplayManager.switchActiveGraph();

		setBorderStyle((Button) arg0.getSource());

	}

	/**
	 * Handler for the Mapping Layer switch Button.
	 */
	public static final void mappingAction(ActionEvent arg0) {

		switchfromSymbolLayer();

		GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
		GraphDisplayManager.switchActiveGraph();

		setBorderStyle((Button) arg0.getSource());

	}

	/**
	 * Handler for the Symbol Representation Layer switch Button.
	 */
	public static final void symbolRepAction(ActionEvent arg0) {

		if (!(GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL))) {

			// add a copy of the underlay graph to the the symbol layer
			MyGraph gClone = (MyGraph) Graphs.clone(GraphDisplayManager.getGraphManager(Layer.UNDERLAY).getGraph());
			gClone.removeAttribute("layer");
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.addGraph(gClone, true);

			activateWorldView();

		}

		GraphDisplayManager.switchActiveGraph();
		setBorderStyle((Button) arg0.getSource());
	}

	/**
	 * Initializes the WorldView, sets data and paints them
	 */
	private static void activateWorldView() {

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
	 * Functionality for "edge visible" Checkbox
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
	 * Functionality for "label visible" Checkbox
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

		// Show node labels
		if (newVal) {
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
	 * Functionality for "edge weights visible" Checkbox
	 * 
	 * @param ov
	 * @param oldVal
	 *            Checkbox previous state (Checked or unchecked)
	 * @param newVal
	 *            Checkbox current state (Checked or unchecked)
	 */
	public static void edgeWeightVisibilitySwitcher(ObservableValue<? extends Boolean> ov, Boolean oldVal,
			Boolean newVal) {

		// Show Edges weights
		if (newVal) {
			WorldView.edgePainter.setShowWeights(true);
			WorldView.internMapViewer.repaint();
			// Hide Edges weights
		} else {
			WorldView.edgePainter.setShowWeights(false);
			WorldView.internMapViewer.repaint();
		}
	}

	/**
	 * Changes the border of the button that was pressed to red
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
	 * update mapViewer if choiceBox item was changed
	 * 
	 * @param ov
	 * @param oldVal
	 * @param newVal
	 */
	public static void mapViewChoiceChange(ObservableValue<? extends String> ov, String oldVal, String newVal) {
		MapViewFunctions.changeMapView();
	}

}
