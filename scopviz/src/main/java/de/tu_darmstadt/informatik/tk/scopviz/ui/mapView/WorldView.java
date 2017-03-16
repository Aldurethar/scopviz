package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.graphstream.graph.Edge;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;

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
	 * GUIController with UI elements
	 */
	public static GUIController controller;

	/*
	 * All waypoints in the WorldView
	 */
	public static HashSet<CustomWaypoint> waypoints;

	/*
	 * All edges in the WorldView
	 */
	public static HashSet<Edge> edges;

	public static Thread threadBeforeMapViewLoaded;

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

		threadBeforeMapViewLoaded = Thread.currentThread();

		HashSet<GeoPosition> nodePositions = new HashSet<GeoPosition>();
		waypoints = new HashSet<CustomWaypoint>();
		edges = new HashSet<Edge>();

		// Get GeoPositions of nodes and get all waypoints created
		MapViewFunctions.fetchGraphData(nodePositions, waypoints, edges);

		// Create a line for all edges
		edgePainter = new EdgePainter(edges);

		// Create a waypoint painter that takes all the waypoints
		waypointPainter = new WaypointPainter<CustomWaypoint>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new CustomWaypointRenderer());

		// Create a compound painter that uses all painters
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(edgePainter);
		painters.add(waypointPainter);

		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();

		// try to load OpenStreesMap, when errors occur, throw and handle
		// Exceptions
		URL osmWebPage;
		try {
			// try to connect to OpenStreetMap server
			osmWebPage = new URL(info.getBaseURL());
			URLConnection connection = osmWebPage.openConnection();
			connection.connect();

		} catch (MalformedURLException e) {
			// TODO add Dialog with eroor msg and stack trace
			e.printStackTrace();

		}

		CustomTileFactory tileFactory = new CustomTileFactory(info);
		internMapViewer.setTileFactory(tileFactory);

		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		// set Zoom and Center to show all node positions
		internMapViewer.zoomToBestFit(nodePositions, 0.7);

		internMapViewer.setOverlayPainter(painter);

		// "click on waypoints" listener
		internMapViewer.addMouseListener(new CustomMapClickListener(internMapViewer));

		internMapViewer.repaint();
	}

}
