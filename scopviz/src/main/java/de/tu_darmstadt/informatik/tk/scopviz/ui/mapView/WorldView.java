package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyEdge;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyNode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WorldView {

	/*
	 * intern map viewer
	 */
	public static JXMapViewer internMapViewer;

	/*
	 * edgePainter of overlayPainter
	 */
	public static EdgePainter edgePainter;

	/*
	 * waypointPointer of overlayPainter
	 */
	public static WaypointPainter<CustomWaypoint> waypointPainter;

	/*
	 * mapClickListener, used to only initialize the Listener once
	 */
	public static CustomMapClickListener mapClickListener;

	/*
	 * GUIController with UI elements
	 */
	public static GUIController controller;

	/*
	 * All waypoints in the WorldView
	 */
	public static HashSet<CustomWaypoint> waypoints;

	/**
	 * the waypoints represented as an ordered list
	 */
	public static ArrayList<CustomWaypoint> waypointsAsList;

	/*
	 * All edges in the WorldView
	 */
	public static HashSet<MyEdge> edges;

	public static HashSet<GeoPosition> nodePositions;

	/*
	 * All painter in symbolLayer stored in a list
	 */
	public static List<Painter<JXMapViewer>> painters;

	/**
	 * private constructor to avoid instantiation
	 */
	private WorldView() {
	}

	/**
	 * initialize attributes internMapViewer and controller
	 * 
	 * @param mapViewer
	 * @param guiController
	 */
	public static void initAttributes(JXMapViewer mapViewer, GUIController guiController) {
		internMapViewer = mapViewer;
		controller = guiController;
	}

	/**
	 * load map elements based on current underlay graph
	 * 
	 * @throws IOException
	 */
	public static void loadWorldView() throws IOException {

		nodePositions = new HashSet<GeoPosition>();
		waypoints = new HashSet<CustomWaypoint>();
		edges = new HashSet<MyEdge>();
		waypointsAsList = new ArrayList<CustomWaypoint>();

		// Get GeoPositions of nodes and get all waypoints created
		fetchGraphData();

		// underlay is empty
		if (waypoints.size() == 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("Underlay Empty");
			alert.setContentText("The referenced Underlay-Graph has no nodes to visualize");
			alert.showAndWait();

			GeoPosition defaultGeoPos = new GeoPosition(OptionsManager.getDefaultLat(),
					OptionsManager.getDefaultLong());
			nodePositions.add(defaultGeoPos);

			CustomWaypoint defaultWaypoint = new CustomWaypoint("", "", getDeviceTypeURL(""), "", defaultGeoPos);
			waypoints.add(defaultWaypoint);
			waypointsAsList.add(defaultWaypoint);
		}

		MapViewFunctions.initializeWaypointImages();

		// Create a line for all edges
		edgePainter = new EdgePainter(edges);

		// Create a waypoint painter that takes all the waypoints
		waypointPainter = new WaypointPainter<CustomWaypoint>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new CustomWaypointRenderer());

		// Create a compound painter that uses all painters
		painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(edgePainter);
		painters.add(waypointPainter);

		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();

		if (!internMapViewer.getTileFactory().equals(null)) {
			CustomTileFactory tileFactory = new CustomTileFactory(info);
			internMapViewer.setTileFactory(tileFactory);
		}

		// Use 8 threads in parallel to load the tiles
		((CustomTileFactory) internMapViewer.getTileFactory()).setThreadPoolSize(8);

		showAllWaypoints(nodePositions);

		internMapViewer.setOverlayPainter(painter);

		// set listener the first time
		if (mapClickListener == null) {
			mapClickListener = new CustomMapClickListener(internMapViewer);

			// "click on waypoints" listener
			internMapViewer.addMouseListener(mapClickListener);
		}
		// update listener
		else {
			internMapViewer.removeMouseListener(mapClickListener);

			mapClickListener = new CustomMapClickListener(internMapViewer);

			internMapViewer.addMouseListener(mapClickListener);
		}

		internMapViewer.repaint();

		// try to load OpenStreesMap, when errors occur, throw and handle
		// Exceptions
		URL osmWebPage;
		try {
			// try to connect to OpenStreetMap server
			osmWebPage = new URL(info.getBaseURL());
			URLConnection connection = osmWebPage.openConnection();
			connection.connect();

		} catch (MalformedURLException e) {
			e.printStackTrace();

		}
	}

	/**
	 * centers map, so that all waypoints are shown
	 * 
	 * @param positions
	 */
	public static void showAllWaypoints(HashSet<GeoPosition> positions) {

		ArrayList<Point2D> points = new ArrayList<Point2D>(positions.size());

		internMapViewer.setZoom(1);

		if (positions.size() == 1) {
			internMapViewer.setCenterPosition(positions.iterator().next());
			return;
		}

		Double defaultLat = null;
		Double defaultLong = null;
		Boolean onLineLat = true;
		Boolean onLineLong = true;

		// geoPositions are all on one line -> JXMapViewer2 method doesnt work
		for (GeoPosition geoPos : positions) {
			if (defaultLat == null && defaultLong == null) {
				defaultLat = geoPos.getLatitude();
				defaultLong = geoPos.getLongitude();
			}

			// there is a geoPosition with a different Latitude then the last
			// one
			if (!defaultLat.equals(geoPos.getLatitude())) {
				onLineLat = false;
			}
			// there is a geoPosition with a different Longitude then the last
			// one
			if (!defaultLong.equals(geoPos.getLongitude())) {
				onLineLong = false;
			}
		}

		// geoPositions all have the same Latitude
		if (onLineLat && !onLineLong) {
			HashSet<GeoPosition> newPositions = new HashSet<GeoPosition>();
			newPositions.addAll(positions);

			GeoPosition newGeoPos = new GeoPosition(positions.iterator().next().getLatitude() + 0.00001,
					positions.iterator().next().getLongitude());
			newPositions.add(newGeoPos);

			internMapViewer.calculateZoomFrom(newPositions);

		} else if (onLineLong && !onLineLat) {
			// geoPositions all have the same Longitude
			HashSet<GeoPosition> newPositions = new HashSet<GeoPosition>();
			newPositions.addAll(positions);

			GeoPosition newGeoPos = new GeoPosition(positions.iterator().next().getLatitude(),
					positions.iterator().next().getLongitude() + 0.00001);
			newPositions.add(newGeoPos);

			internMapViewer.calculateZoomFrom(newPositions);

		} else {
			// geoPositions have different Latitude and Longitude
			internMapViewer.calculateZoomFrom(positions);
		}

		positions.forEach((geoPos) -> points.add(internMapViewer.convertGeoPositionToPoint(geoPos)));

		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;

		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (Point2D p : points) {
			if (p.getX() < minX) {
				minX = p.getX();
			}
			if (p.getX() > maxX) {
				maxX = p.getX();
			}

			if (p.getY() < minY) {
				minY = p.getY();
			}
			if (p.getY() > maxY) {
				maxY = p.getY();
			}
		}

		Rectangle2D rect = new Rectangle2D(minX, minY, maxY - minY, maxX - minX);

		double xPos = rect.getMinX() + rect.getHeight() / 2;
		double yPos = rect.getMinY() + rect.getWidth() / 2;

		Point2D center = new Point2D.Double(xPos, yPos);

		internMapViewer.setCenterPosition(internMapViewer.convertPointToGeoPosition(center));

	}

	/**
	 * Initialize HashSets with data from graph
	 * 
	 * @param nodePositions
	 *            Read node data to create GeoPositions of all nodes
	 * @param waypoints
	 *            Read node data to create CustomWaypoints with deviceTypes
	 */
	public static void fetchGraphData() {

		GraphManager man = GraphDisplayManager.getGraphManager(Layer.UNDERLAY);

		// add all edges from the Graph to the HashSet
		for (MyEdge edge : man.getGraph().<MyEdge>getEdgeSet()) {
			edges.add(edge);
		}

		// fetch all needed data from nodes
		for (MyNode node : man.getGraph().<MyNode>getEachNode()) {

			if (node.hasAttribute("lat") && node.hasAttribute("long")) {

				// Fetch all geo-data from nodes
				Double latitude = node.getAttribute("lat");
				Double longitude = node.getAttribute("long");

				GeoPosition geoPos = new GeoPosition(latitude.doubleValue(), longitude.doubleValue());

				nodePositions.add(geoPos);

				// Create waypoints with device type dependent pictures
				String deviceType = (String) node.getAttribute("typeofDevice");
				URL resource = getDeviceTypeURL(deviceType);

				// create a new waypoint with the node information
				CustomWaypoint waypoint = new CustomWaypoint(node.getAttribute("ui.label"), node.getId(), resource,
						deviceType, geoPos);

				waypoints.add(waypoint);

				waypointsAsList.add(waypoint);
			}
		}

	}

	/**
	 * get the png URL based on the device.type of nodes
	 * 
	 * @param deviceType
	 * @return
	 */
	public static URL getDeviceTypeURL(String deviceType) {

		URL image = MainApp.class
				.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/mapView/symbol_icons/" + deviceType + ".png");

		if (image == null) {
			return MainApp.class
					.getResource("/de/tu_darmstadt/informatik/tk/scopviz/ui/mapView/symbol_icons/not_found.png");
		}

		else {
			return image;
		}

	}

	public static HashSet<CustomWaypoint> getWaypoints() {
		return waypoints;
	}

	public static HashSet<GeoPosition> getNodePositions() {
		return nodePositions;
	}

	public static HashSet<MyEdge> getEdges() {
		return edges;
	}

	public static ArrayList<CustomWaypoint> getWaypointsAsArrayList() {
		return waypointsAsList;
	}

}
