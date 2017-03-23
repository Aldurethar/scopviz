package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.HashSet;

import org.graphstream.graph.Edge;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Paints a route
 * 
 * @author Dominik Renkel
 */
public class EdgePainter implements Painter<JXMapViewer> {

	/**
	 * show edges property
	 */
	private boolean showEdges = true;

	/**
	 * standard color in which edges are drawn
	 */
	private static final Color STANDARD = Color.BLACK;

	/**
	 * color in which edges are drawn when clicked
	 */
	private static final Color CLICKED = Color.RED;

	/**
	 * color in which edges are drawn when they are used in a placement
	 */
	private static final Color PLACEMENT = Color.BLUE;

	/**
	 * anti aliasing property
	 */
	private boolean antiAlias = true;

	/**
	 * the edges of the currently shown graph
	 */
	private static HashSet<Edge> edges;

	/**
	 * show weights property
	 */
	private Boolean showWeights = true;

	/**
	 * @param track
	 *            the track
	 */
	public EdgePainter(HashSet<Edge> track) {
		// copy the list so that changes in the
		// original list do not have an effect here
		edges = new HashSet<Edge>(track);
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer mapViewer, int w, int h) {

		if (showEdges) {
			g = (Graphics2D) g.create();

			// convert from viewport to world bitmap
			Rectangle rect = mapViewer.getViewportBounds();
			g.translate(-rect.x, -rect.y);

			if (antiAlias)
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// do the drawing again
			g.setColor(STANDARD);
			g.setStroke(new BasicStroke(2));

			drawRoute(g, mapViewer);

			g.dispose();
		}
	}

	/**
	 * Draws a line on the street map for all edges
	 * 
	 * @param g
	 *            the graphics object
	 * @param mapViewer
	 *            the map
	 */
	private void drawRoute(Graphics2D g, JXMapViewer mapViewer) {

		for (Edge edge : edges) {

			// Get geo Positions of the two nodes that define the edge
			GeoPosition startPos = new GeoPosition(edge.getNode0().getAttribute("lat"),
					edge.getNode0().getAttribute("long"));
			GeoPosition endPos = new GeoPosition(edge.getNode1().getAttribute("lat"),
					edge.getNode1().getAttribute("long"));

			// convert geo-coordinate to world bitmap pixel
			Point2D startPoint = mapViewer.getTileFactory().geoToPixel(startPos, mapViewer.getZoom());
			Point2D endPoint = mapViewer.getTileFactory().geoToPixel(endPos, mapViewer.getZoom());

			if (edge.hasAttribute("ui.map.selected") && (boolean) edge.getAttribute("ui.map.selected")) {
				// draw red line if edge is selected
				g.setColor(CLICKED);

			} else if (edge.hasAttribute("usedInPlacement") && (boolean) edge.getAttribute("usedInPlacement")) {
				// draw blue line when edge used in placement
				g.setColor(PLACEMENT);

			} else {
				// draw black line if not selected
				g.setColor(STANDARD);
			}

			g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) endPoint.getX(), (int) endPoint.getY());

			if (showWeights) {
				drawWeights(edge, g, startPoint, endPoint);
			}
		}
	}

	/**
	 * draw the weights of an edge
	 * 
	 * @param edge
	 *            edge
	 * @param g
	 *            graphic
	 * @param startPoint
	 *            start point edge
	 * @param endPoint
	 *            end point edge
	 */
	private void drawWeights(Edge edge, Graphics2D g, Point2D startPoint, Point2D endPoint) {
		// Set weight Position on street map
		String weight = edge.getAttribute("weight").toString();

		// get weight height and width under given font
		FontMetrics metrics = g.getFontMetrics();
		int tw = metrics.stringWidth(weight);
		int th = 1 + metrics.getAscent();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double deltaX = (startPoint.getX() - endPoint.getX()) / 2;
		double deltaY = (startPoint.getY() - endPoint.getY()) / 2;

		double weightPosX;
		double weightPosY;

		if (deltaX < 0) {
			weightPosX = startPoint.getX() + Math.abs(deltaX);
		} else {
			weightPosX = startPoint.getX() - Math.abs(deltaX);
		}

		if (deltaY < 0) {
			weightPosY = startPoint.getY() + Math.abs(deltaY);
		} else {
			weightPosY = startPoint.getY() - Math.abs(deltaY);
		}
		// Show weight left middle of deviceType picture
		g.drawString(weight, (int) weightPosX - tw / 2, (int) weightPosY - th / 2);

	}

	/**
	 * Sets the showEdges attribute
	 * 
	 * @param showEdges
	 */
	public void setShowEdges(Boolean showEdges) {
		this.showEdges = showEdges;
	}

	/**
	 * sets the showWeights attribute
	 * 
	 * @param showWeights
	 */
	public void setShowWeights(Boolean showWeights) {
		this.showWeights = showWeights;
	}

}
