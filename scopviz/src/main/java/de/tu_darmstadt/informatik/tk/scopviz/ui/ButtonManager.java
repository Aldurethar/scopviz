package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.Graphs;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomMapClickListener;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypoint;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.CustomWaypointRenderer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.EdgePainter;
import de.tu_darmstadt.informatik.tk.scopviz.ui.mapView.MapViewFunctions;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public final class ButtonManager {

	/** Flag for creating more then one Edge at a time mode */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/** List of the Buttons for Layer switching */
	private static ArrayList<Button> layerButtons;

	private static GUIController controller;

	private static JXMapViewer internMapViewer;

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
	 */
	public static void initialize(ArrayList<Button> nList, GUIController guiController, Button uButton) {
		layerButtons = nList;

		controller = guiController;
		setBorderStyle(uButton);
	}

	public static void setViewer(JXMapViewer mapViewer) {
		internMapViewer = mapViewer;
	}

	/**
	 * Handler for zoom in Button
	 */
	public static final void zoomInAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			internMapViewer.setZoom(internMapViewer.getZoom() - 1);
		} else {
			Main.getInstance().getGraphManager().zoomIn();
		}
	}

	/**
	 * Handler for zoom out Button
	 */
	public static final void zoomOutAction(ActionEvent event) {
		if (GraphDisplayManager.getCurrentLayer().equals(Layer.SYMBOL)) {
			internMapViewer.setZoom(internMapViewer.getZoom() + 1);
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
			controller.toolbox.setVisible(true);
			controller.symbolToolVBox.setVisible(false);

			controller.propertiesObjectColumn.setEditable(true);

			controller.swingNodeWorldView.setVisible(false);
			controller.swingNode.setVisible(true);

			controller.stackPane.setMouseTransparent(true);
			controller.swingNodeWorldView.setMouseTransparent(true);

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
			controller.toolbox.setVisible(false);
			controller.swingNode.setVisible(false);
			controller.propertiesObjectColumn.setEditable(false);
			controller.stackPane.setMouseTransparent(false);
			controller.swingNodeWorldView.setMouseTransparent(false);

			controller.pane.setMouseTransparent(true);
			controller.swingNode.setMouseTransparent(true);
			controller.symbolToolVBox.setVisible(true);

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

		HashSet<GeoPosition> nodePositions = new HashSet<GeoPosition>();
		HashSet<CustomWaypoint> waypoints = new HashSet<CustomWaypoint>();
		HashSet<Edge> edges = new HashSet<Edge>();

		// Get GeoPositions of nodes and get all waypoints created
		MapViewFunctions.fetchGraphData(nodePositions, waypoints, edges);

		// Create a line for all edges
		EdgePainter edgePainter = new EdgePainter(edges);

		// Create a waypoint painter that takes all the waypoints
		WaypointPainter<CustomWaypoint> waypointPainter = new WaypointPainter<CustomWaypoint>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new CustomWaypointRenderer());

		// Create a compound painter that uses all painters
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(waypointPainter);
		painters.add(edgePainter);

		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		internMapViewer.setTileFactory(tileFactory);

		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		// set Zoom and Center to show all node positions
		internMapViewer.zoomToBestFit(nodePositions, 0.7);

		internMapViewer.setOverlayPainter(painter);

		// "click on waypoints" listener
		internMapViewer.addMouseListener(new CustomMapClickListener(internMapViewer, waypoints, edges));

		MapViewFunctions.checkVBoxChanged();

		internMapViewer.repaint();

		controller.swingNodeWorldView.setContent(internMapViewer);
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
	public static void edgeVisibleSwitch(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {

		EdgePainter edgePainter = (EdgePainter) MapViewFunctions.getRequestedPainter("edge");

		// Show edges
		if (newVal) {
			edgePainter.setShowEdges(true);
			internMapViewer.repaint();
			// Hide edges
		} else {
			edgePainter.setShowEdges(false);
			internMapViewer.repaint();
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

		WaypointPainter<CustomWaypoint> waypointPainter = (WaypointPainter<CustomWaypoint>) MapViewFunctions
				.getRequestedPainter("waypoint");

		CustomWaypointRenderer renderer = new CustomWaypointRenderer();

		// Show node labels
		if (newVal) {
			renderer.setShowLabels(true);
			waypointPainter.clearCache();
			waypointPainter.setRenderer(renderer);
			internMapViewer.repaint();
			// Hide node labels
		} else {
			renderer.setShowLabels(false);
			waypointPainter.clearCache();
			waypointPainter.setRenderer(renderer);
			internMapViewer.repaint();
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

		EdgePainter edgePainter = (EdgePainter) MapViewFunctions.getRequestedPainter("edge");
		// Show Edges weights
		if (newVal) {
			edgePainter.setShowWeights(true);
			internMapViewer.repaint();
			// Hide Edges weights
		} else {
			edgePainter.setShowWeights(false);
			internMapViewer.repaint();
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
						"-fx-background-color: red, red, red, -fx-faint-focus-color, -fx-body-color; -fx-background-insets: -0.2, 1, 2, -1.4, 2.6; -fx-background-radius: 3, 2, 1, 4, 1;");
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
