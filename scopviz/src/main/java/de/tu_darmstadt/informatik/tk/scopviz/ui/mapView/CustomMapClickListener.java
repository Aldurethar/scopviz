package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.GeoPosition;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.EdgeSelectionHelper;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;

public class CustomMapClickListener extends MapClickListener {

	/*
	 * World view viewer
	 */
	private static JXMapViewer viewer;

	/*
	 * selected waypoint
	 */
	public static CustomWaypoint selectedNode;

	/*
	 * selected edge
	 */
	public static Edge selectedEdge;

	/*
	 * all edges of the graph
	 */
	private final static HashSet<Edge> edges = WorldView.edges;

	/*
	 * all waypoints of the graph
	 */
	private final static HashSet<CustomWaypoint> waypoints = WorldView.waypoints;

	/**
	 * Constructor sets viewer
	 * 
	 * @param viewer
	 */
	public CustomMapClickListener(JXMapViewer viewer) {
		super(viewer);

		CustomMapClickListener.viewer = viewer;

	}

	@Override
	public void mapClicked(GeoPosition arg0) {

		Point2D clickedPoint = CustomMapClickListener.viewer.getTileFactory().geoToPixel(arg0,
				CustomMapClickListener.viewer.getZoom());
		Point2D nodePoint;

		// a waypoint was clicked
		Boolean wayPointSelected = false;

		wayPointSelected = checkWaypointClicked(clickedPoint, wayPointSelected);

		// no node selected so check if edge selected
		if (!wayPointSelected) {
			checkEdgeClicked(clickedPoint);
		}

	}

	/**
	 * check if waypoint was clicked in symbolLayer
	 * 
	 * @param clickedPoint
	 *            on map
	 * @param wayPointSelected
	 * @return
	 */
	public Boolean checkWaypointClicked(Point2D clickedPoint, Boolean wayPointSelected) {
		Point2D nodePoint;

		for (CustomWaypoint nodeWaypoint : CustomMapClickListener.waypoints) {
			// transform GeoPosition to point on screen
			nodePoint = CustomMapClickListener.viewer.getTileFactory().geoToPixel(nodeWaypoint.getPosition(),
					CustomMapClickListener.viewer.getZoom());

			boolean yChecked = false;

			// clicked position is in range of 50 pixel above the waypoint
			// position
			double deltaY = clickedPoint.getY() - nodePoint.getY();
			if (deltaY > -50 && deltaY < 0) {
				yChecked = true;
			}

			// clicked Position is in x- and y-range of wapoint position (in
			// range of 50 pixels)
			if (Math.abs(clickedPoint.getX() - nodePoint.getX()) < 25 && yChecked) {

				wayPointSelected = true;

				PropertiesManager.showNewDataSet(GraphDisplayManager.getGraphManager(Layer.UNDERLAY).getGraph()
						.getNode(nodeWaypoint.getNodeID()));

				// deselect old waypoint and select new clicked waypoint
				deselectAll();
				nodeWaypoint.select();
				selectedNode = nodeWaypoint;
				viewer.repaint();
				break;
			}
		}

		return wayPointSelected;
	}

	/**
	 * check if edge was clicked in symbolLayer
	 * 
	 * @param clickedPoint
	 */
	public void checkEdgeClicked(Point2D clickedPoint) {

		// max distance between clicked point and edge to select edge
		double maxDistance = 10.0;

		Edge result = null;

		for (Edge edge : CustomMapClickListener.edges) {
			// Get geo Positions of the two nodes that define the edge
			GeoPosition startPos = new GeoPosition(edge.getNode0().getAttribute("lat"),
					edge.getNode0().getAttribute("long"));
			GeoPosition endPos = new GeoPosition(edge.getNode1().getAttribute("lat"),
					edge.getNode1().getAttribute("long"));

			// convert geo-coordinate to world bitmap pixel
			Point2D startPoint = viewer.getTileFactory().geoToPixel(startPos, viewer.getZoom());
			Point2D endPoint = viewer.getTileFactory().geoToPixel(endPos, viewer.getZoom());

			// the actual edge between the points
			Line2D.Double line = new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(),
					endPoint.getY());

			// distance between nodes
			double distanceBetweenNodes = EdgeSelectionHelper.distance(startPoint.getX(), startPoint.getY(),
					endPoint.getX(), endPoint.getY());

			// distance between clicked point and edge
			double distanceClickedAndEdge = line.ptLineDist(clickedPoint);

			// half pi
			double HALF_PI = Math.PI / 2;

			// distance of clicked point is in range of edge selection (or is nearer then to previous selected edge)
			if (distanceClickedAndEdge < maxDistance) {

				// distance start point to clicked point
				double distanceStartToClicked = EdgeSelectionHelper.distance(startPoint.getX(), startPoint.getY(),
						clickedPoint.getX(), clickedPoint.getY());
				
				// distance end point to clicked point
				double distanceEndToClicked = EdgeSelectionHelper.distance(endPoint.getX(), endPoint.getY(),
						clickedPoint.getX(), clickedPoint.getY());

				// square distances
				double a2 = distanceStartToClicked * distanceStartToClicked;
				double b2 = distanceEndToClicked * distanceEndToClicked;
				double c2 = distanceBetweenNodes * distanceBetweenNodes;

				// Calculates the inner angles off the triangle
				double alpha = Math.acos((b2 + c2 - a2) / (2 * distanceEndToClicked * distanceBetweenNodes));
				double beta = Math.acos((a2 + c2 - b2) / (2 * distanceStartToClicked * distanceBetweenNodes));

				
				// Check if the point is actually visually next to the edge by
				// checking if both inner angles are less than 90°
				if (alpha <= HALF_PI && beta <= HALF_PI) {
					maxDistance = distanceClickedAndEdge;
					result = edge;
				}
			}
		}

		// Clicked point is in range of edge selection
		if (result != null) {

			PropertiesManager.showNewDataSet(result);

			deselectAll();

			if (!result.hasAttribute("ui.map.selected"))
				result.addAttribute("ui.map.selected", true);
			else
				result.changeAttribute("ui.map.selected", true);

			selectedEdge = result;

			viewer.repaint();
		}

	}

	/**
	 * deselect all edges and the selected node
	 */
	public static void deselectAll() {
		if (selectedNode != null) {

			selectedNode.deselect();
			selectedNode = null;
		}
		if (selectedEdge != null) {

			selectedEdge.changeAttribute("ui.map.selected", false);
			selectedEdge = null;
		}

		viewer.repaint();
	}

}
