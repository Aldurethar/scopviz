package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.MainApp;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;

public final class MapViewFunctions {

	/**
	 * private constructor to avoid instantiation
	 */
	private MapViewFunctions() {

	}

	/**
	 * Scale a given BufferedImage down to given width w and given height h
	 * 
	 * @param loadImg
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage loadImg, int w, int h) {
		BufferedImage imgOut = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = imgOut.createGraphics();
		graphics.drawImage(loadImg, 0, 0, w, h, null);
		graphics.dispose();

		return imgOut;
	}

	/**
	 * change all pixels from a given color to another given color (under a
	 * given alpha value)
	 *
	 * @param image
	 * @param toChange
	 *            change all pixels with this color to another color
	 * @param changeWith
	 *            new color
	 * @param alpha
	 * @return
	 */
	public static BufferedImage colorImage(BufferedImage image, Color toChange, Color changeWith, int alpha) {
		int width = image.getWidth();
		int height = image.getHeight();

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				Color originalColor = new Color(image.getRGB(xx, yy), true);

				// Pixel is Black -> paint it Red
				if (originalColor.equals(toChange) && originalColor.getAlpha() == alpha) {
					image.setRGB(xx, yy, changeWith.getRGB());
				}
			}
		}
		return image;
	}

	/**
	 * Initialize HashSets with data from graph
	 * 
	 * @param nodePositions
	 *            Read node data to create GeoPositions of all nodes
	 * @param waypoints
	 *            Read node data to create CustomWaypoints with deviceTypes
	 */
	public static void fetchGraphData(HashSet<GeoPosition> nodePositions, HashSet<CustomWaypoint> waypoints,
			HashSet<Edge> edges) {

		GraphManager man = GraphDisplayManager.getGraphManager(Layer.UNDERLAY);

		// add all edges from the Graph to the HashSet
		for (Edge edge : man.getGraph().getEdgeSet()) {
			edges.add(edge);
		}

		// fetch all needed data from nodes
		for (Node node : man.getGraph().getEachNode()) {

			if (node.hasAttribute("lat") && node.hasAttribute("long")) {

				// Fetch all geo-data from nodes
				Double latitude = node.getAttribute("lat");
				Double longitude = node.getAttribute("long");

				GeoPosition geoPos = new GeoPosition(latitude.doubleValue(), longitude.doubleValue());

				nodePositions.add(geoPos);

				// Create waypoints with device type dependent pictures
				String deviceType = (String) node.getAttribute("device.type");
				URL resource = getDeviceType(deviceType);

				// create a new waypoint with the node information
				waypoints.add(new CustomWaypoint(node.getAttribute("ui.label"), node.getId(), resource, geoPos));

			}
		}
	}

	/**
	 * get the png URL based on the device.type of nodes
	 * 
	 * @param deviceType
	 * @return
	 */
	public static URL getDeviceType(String deviceType) {
		// TODO add pngs for device types
		switch (deviceType.equals(null) ? "" : deviceType) {

		case "3G_lte_basestation":
			return MainApp.class.getResource("/png/symbol_icons/3G_lte_basestation.png");

		case "car":
			return MainApp.class.getResource("/png/symbol_icons/car.png");

		case "cloud_computing_server":
			return MainApp.class.getResource("/png/symbol_icons/cloud_computing_server.png");

		case "data_storage":
			return MainApp.class.getResource("/png/symbol_icons/data_storage.png");

		case "desktop":
			return MainApp.class.getResource("/png/symbol_icons/desktop.png");

		case "laptop":
			return MainApp.class.getResource("/png/symbol_icons/laptop.png");

		case "network_middlebox":
			return MainApp.class.getResource("/png/symbol_icons/network_middlebox.png");

		case "raspberry_pi":
			return MainApp.class.getResource("/png/symbol_icons/raspberry_pi.png");

		case "router":
			return MainApp.class.getResource("/png/symbol_icons/router.png");

		case "sensor":
			return MainApp.class.getResource("/png/symbol_icons/sensor.png");

		case "smart_home":
			return MainApp.class.getResource("/png/symbol_icons/smart_home.png");

		case "smartband":
			return MainApp.class.getResource("/png/symbol_icons/smartband.png");

		case "smartphone":
			return MainApp.class.getResource("/png/symbol_icons/smartphone.png");

		case "smartwatch":
			return MainApp.class.getResource("/png/symbol_icons/smartwatch.png");

		case "switch":
			return MainApp.class.getResource("/png/symbol_icons/switch.png");

		case "tablet":
			return MainApp.class.getResource("/png/symbol_icons/tablet.png");

		default:
			return MainApp.class.getResource("/png/symbol_icons/unknown.png");

		}
	}

	/**
	 * Returns either an EdgePainter (case input "edge") or a WaypointPainter
	 * (case input "waypoint") based on input
	 * 
	 * @param mode
	 *            0 or 1
	 * @return EdgePainter or WaypointPainter if existing in CompoundPainter
	 *         otherwise null
	 */
	private static Painter<JXMapViewer> getRequestedPainter(String requested) {

		// return value
		switch (requested) {
		case "edge":
			return WorldView.edgePainter;
		case "waypoint":
			return WorldView.waypointPainter;
		default:
			return null;

		}

	}

	/**
	 * change the shown map based on the selected item in the ChoiceBox
	 */
	public static void changeMapView() {
		String selected = WorldView.controller.mapViewChoiceBox.getSelectionModel().getSelectedItem();

		switch (selected) {
		case "Default":
			TileFactoryInfo defaultTileFactoryInfo = new OSMTileFactoryInfo();
			WorldView.internMapViewer.setTileFactory(new DefaultTileFactory(defaultTileFactoryInfo));
			break;

		case "Road":
			TileFactoryInfo roadTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
			WorldView.internMapViewer.setTileFactory(new DefaultTileFactory(roadTileFactoryInfo));
			break;

		case "Satellite":
			TileFactoryInfo sateliteTileFactoryInfo = new VirtualEarthTileFactoryInfo(
					VirtualEarthTileFactoryInfo.SATELLITE);
			WorldView.internMapViewer.setTileFactory(new DefaultTileFactory(sateliteTileFactoryInfo));
			break;

		case "Hybrid":
			TileFactoryInfo hybridTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
			WorldView.internMapViewer.setTileFactory(new DefaultTileFactory(hybridTileFactoryInfo));
			break;
		}
	}

	/**
	 * Check if Checkboxes or ChoiceBox where changed, last time symbol-rep.
	 * layer was shown
	 */
	public static void checkVBoxChanged() {

		EdgePainter edgePainter = (EdgePainter) getRequestedPainter("edge");
		WaypointPainter<CustomWaypoint> waypointPainter = (WaypointPainter<CustomWaypoint>) getRequestedPainter(
				"waypoint");

		// Checkboxes were changed last time symbolRep-Layer was shown
		if (!WorldView.controller.edgesVisibleCheckbox.isSelected()) {
			edgePainter.setShowEdges(false);
		}
		if (!WorldView.controller.nodeLabelCheckbox.isSelected()) {
			CustomWaypointRenderer renderer = new CustomWaypointRenderer();
			renderer.setShowLabels(false);
			waypointPainter.setRenderer(renderer);
		}
		if (!WorldView.controller.edgeWeightCheckbox.isSelected()) {
			edgePainter.setShowWeights(false);
		}
		if (!WorldView.controller.mapViewChoiceBox.getSelectionModel().getSelectedItem().equals("Default")) {
			MapViewFunctions.changeMapView();
		}
	}

}
