package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.GeoPosition;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
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

		Point2D clickedPoint = CustomMapClickListener.viewer.getTileFactory().geoToPixel(arg0, CustomMapClickListener.viewer.getZoom());
		Point2D nodePoint;

		// a waypoint was clicked
		Boolean wayPointSelected = false;

		for (CustomWaypoint nodeWaypoint : CustomMapClickListener.waypoints) {
			// transform GeoPosition to point on screen
			nodePoint = CustomMapClickListener.viewer.getTileFactory().geoToPixel(nodeWaypoint.getPosition(), CustomMapClickListener.viewer.getZoom());

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
					
					PropertiesManager.showNewDataSet(edge);
					
					deselectAll();
					
					if (!edge.hasAttribute("ui.map.selected"))
						edge.addAttribute("ui.map.selected", true);
					else
						edge.changeAttribute("ui.map.selected", true);
					
					selectedEdge = edge;

					viewer.repaint();
					break;
				} else {
					if(edge.hasAttribute("ui.map.selected")){
						edge.changeAttribute("ui.map.selected", false);
					}
				}
			}
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
		if(selectedEdge != null){
			
			selectedEdge.changeAttribute("ui.map.selected", false);
			selectedEdge = null;
		}
		
		viewer.repaint();
	}

}
