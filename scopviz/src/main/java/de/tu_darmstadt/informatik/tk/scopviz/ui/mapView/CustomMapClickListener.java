package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.GeoPosition;

import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;

public class CustomMapClickListener extends MapClickListener {

	private final HashSet<CustomWaypoint> nodePositions;
	private final JXMapViewer viewer;

	private static CustomWaypoint selected;

	private static HashSet<Edge> edges;

	public CustomMapClickListener(JXMapViewer viewer, HashSet<CustomWaypoint> waypoints, HashSet<Edge> edges) {
		super(viewer);

		this.viewer = viewer;
		this.nodePositions = waypoints;
		CustomMapClickListener.edges = edges;

	}

	@Override
	public void mapClicked(GeoPosition arg0) {

		Point2D clickedPoint = this.viewer.getTileFactory().geoToPixel(arg0, this.viewer.getZoom());
		Point2D nodePoint;

		// a waypoint was clicked
		Boolean wayPointSelected = false;

		for (CustomWaypoint nodeWaypoint : this.nodePositions) {
			// transform GeoPosition to point on screen
			nodePoint = this.viewer.getTileFactory().geoToPixel(nodeWaypoint.getPosition(), this.viewer.getZoom());

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
				selected = nodeWaypoint;
				nodeWaypoint.select();
				viewer.repaint();
				break;
			}
		}

		// no node selected so check if edge selected
		if (!wayPointSelected) {
			for (Edge edge : CustomMapClickListener.edges) {
				// Get geo Positions of the two nodes that define the edge
				GeoPosition startPos = new GeoPosition(edge.getNode0().getAttribute("lat"),
						edge.getNode0().getAttribute("long"));
				GeoPosition endPos = new GeoPosition(edge.getNode1().getAttribute("lat"),
						edge.getNode1().getAttribute("long"));

				// convert geo-coordinate to world bitmap pixel
				Point2D startPoint = viewer.getTileFactory().geoToPixel(startPos, viewer.getZoom());
				Point2D endPoint = viewer.getTileFactory().geoToPixel(endPos, viewer.getZoom());

				Line2D.Double line = new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(),
						endPoint.getY());

				// Clicked point in 10 pixel range of line
				if (line.ptLineDist(clickedPoint) < 10) {
					deselectAll();
					if (!edge.hasAttribute("ui.map.selected"))
						edge.addAttribute("ui.map.selected", true);
					else
						edge.changeAttribute("ui.map.selected", true);

					PropertiesManager.showNewDataSet(edge);

					viewer.repaint();
					break;
				} else {
					edge.changeAttribute("ui.map.selected", false);
				}
			}
		}

	}

	/**
	 * deselect all edges and the selected node
	 */
	public static void deselectAll() {
		if (selected != null)
			selected.deselect();
		for (Edge edge : edges) {
			edge.changeAttribute("ui.map.selected", false);
		}
	}

}