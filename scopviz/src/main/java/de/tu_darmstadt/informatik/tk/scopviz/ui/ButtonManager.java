package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.event.MouseInputListener;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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

	private static Boolean hostReachable = true;

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

	public static void showHostNotReachableDialog() {
		// Create new Dialog
		Dialog<ArrayList<String>> addPropDialog = new Dialog<>();
		addPropDialog.setTitle("Preferences");

		ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		addPropDialog.getDialogPane().getButtonTypes().add(okButton);

		// create grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// position elements on grid
		grid.add(new Label("Currently no OpenStreetView Host connection"), 0, 0);

		// set dialog
		addPropDialog.getDialogPane().setContent(grid);

		// get new property values
		addPropDialog.setResultConverter(dialogButton -> {

			controller.underlayButton.fire();
			return null;

		});
		addPropDialog.showAndWait();

	}

	/**
	 * Initializes the WorldView, sets data and paints them
	 */
	private static void activateWorldView() {

		HashSet<GeoPosition> nodePositions = new HashSet<GeoPosition>();
		HashSet<CustomWaypoint> waypoints = new HashSet<CustomWaypoint>();
		HashSet<Edge> edges = new HashSet<Edge>();

		// Get GeoPositions of nodes and get all waypoints created
		initializeDataSets(nodePositions, waypoints, edges);

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

		JXMapViewer mapViewer = (JXMapViewer) controller.swingNodeWorldView.getContent();

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapViewer.setTileFactory(tileFactory);

		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		// Add interactions

		// "Drag map around" Listener
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

		// "click on waypoints" listener
		mapViewer.addMouseListener(new CustomMapClickListener(mapViewer, waypoints, edges));

		// center map if double clicked / middle clicked
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));

		// zoom with mousewheel
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

		// TODO make this work
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));

		// set Zoom and Center to show all node positions
		mapViewer.zoomToBestFit(nodePositions, 0.7);

		mapViewer.setOverlayPainter(painter);

		// Checkboxes were changed last time symbolRep-Layer was shown
		if (!controller.edgesVisibleCheckbox.isSelected()) {
			edgePainter.setShowEdges(false);
		}
		if (!controller.nodeLabelCheckbox.isSelected()) {
			CustomWaypointRenderer renderer = new CustomWaypointRenderer();
			renderer.setShowLabels(false);
			waypointPainter.setRenderer(renderer);
		}
		if (!controller.edgeWeightCheckbox.isSelected()) {
			edgePainter.setShowWeights(false);
		}
		if (!controller.mapViewChoiceBox.getSelectionModel().getSelectedItem().equals("Default")) {
			changeMapView();
		}

		internMapViewer = mapViewer;
		internMapViewer.repaint();

		controller.swingNodeWorldView.setContent(internMapViewer);
		controller.swingNodeWorldView.setVisible(true);
	}

	/**
	 * Initialize HashSets with data from graph
	 * 
	 * @param nodePositions
	 *            Read node data to create GeoPositions of all nodes
	 * @param waypoints
	 *            Read node data to create CustomWaypoints with deviceTypes
	 */
	private static void initializeDataSets(HashSet<GeoPosition> nodePositions, HashSet<CustomWaypoint> waypoints,
			HashSet<Edge> edges) {

		GraphManager man = GraphDisplayManager.getGraphManager();

		// add all edges from the Graph to the HashSet
		for (Edge egde : man.getGraph().getEdgeSet()) {
			edges.add(egde);
		}

		for (Node node : man.getGraph().getEachNode()) {

			if (node.hasAttribute("lat") && node.hasAttribute("long")) {

				// Fetch all geo-data from nodes
				Double latitude = node.getAttribute("lat");
				Double longitude = node.getAttribute("long");

				GeoPosition geoPos = new GeoPosition(latitude.doubleValue(), longitude.doubleValue());

				nodePositions.add(geoPos);

				// Create waypoints with device type dependent pictures
				URL resource;
				String deviceType = (String) node.getAttribute("device.type");

				// TODO add pngs for device types
				switch (deviceType.equals(null) ? "" : deviceType) {

				case "mobile":
					resource = MainApp.class.getResource("/png/sink.png");
					break;
				case "desktop":
					resource = MainApp.class.getResource("/png/computer.png");
					break;
				case "router":
					resource = MainApp.class.getResource("/png/router.png");
					break;
				default:
					resource = MainApp.class.getResource("/png/router.png");
					break;
				}

				waypoints.add(new CustomWaypoint(node.getAttribute("ui.label"), node.getId(), resource, geoPos));

			}
		}
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

		EdgePainter edgePainter = (EdgePainter) getCurrentPainter("edge");

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

		WaypointPainter<CustomWaypoint> waypointPainter = (WaypointPainter<CustomWaypoint>) getCurrentPainter(
				"waypoint");

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

		EdgePainter edgePainter = (EdgePainter) getCurrentPainter("edge");
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
	 * Returns either an EdgePainter (case input 0) or a WaypointPainter (case
	 * input 1) based on input
	 * 
	 * @param mode
	 *            0 or 1
	 * @return EdgePainter or WaypointPainter if existing in CompoundPainter
	 *         otherwise null
	 */
	public static Painter<JXMapViewer> getCurrentPainter(String requested) {

		// return types
		EdgePainter edgePainter = null;
		WaypointPainter<CustomWaypoint> waypointPainter = null;

		// currently shown mapViewer
		JXMapViewer mapViewer = (JXMapViewer) controller.swingNodeWorldView.getContent();

		// currently used compound painter
		CompoundPainter<JXMapViewer> compPainter = null;

		if (mapViewer.getOverlayPainter() instanceof CompoundPainter) {
			compPainter = (CompoundPainter<JXMapViewer>) mapViewer.getOverlayPainter();
		}
		// search all painters in compound painter until they found an edge or
		// waypoint painter
		for (Painter<JXMapViewer> painter : compPainter.getPainters()) {
			if (painter instanceof EdgePainter) {
				edgePainter = (EdgePainter) painter;
			} else {
				if (painter instanceof WaypointPainter) {
					waypointPainter = (WaypointPainter<CustomWaypoint>) painter;
				}
			}
		}

		// return value
		switch (requested) {
		case "edge":
			return edgePainter;
		case "waypoint":
			return waypointPainter;
		default:
			return null;

		}

	}

	/**
	 * Set the hostReachable atrribute
	 * 
	 * @param reachable
	 */
	public static void setHostReachable(Boolean reachable) {
		hostReachable = reachable;
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
		changeMapView();
	}

	private static void changeMapView() {
		String selected = controller.mapViewChoiceBox.getSelectionModel().getSelectedItem();

		switch (selected) {
		case "Default":
			TileFactoryInfo defaultTileFactoryInfo = new OSMTileFactoryInfo();
			internMapViewer.setTileFactory(new DefaultTileFactory(defaultTileFactoryInfo));
			break;
		case "Road":
			TileFactoryInfo roadTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
			internMapViewer.setTileFactory(new DefaultTileFactory(roadTileFactoryInfo));
			break;
		case "Satellite":
			TileFactoryInfo sateliteTileFactoryInfo = new VirtualEarthTileFactoryInfo(
					VirtualEarthTileFactoryInfo.SATELLITE);
			internMapViewer.setTileFactory(new DefaultTileFactory(sateliteTileFactoryInfo));
			break;
		case "Hybrid":
			TileFactoryInfo hybridTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
			internMapViewer.setTileFactory(new DefaultTileFactory(hybridTileFactoryInfo));
			break;
		}
	}

}
