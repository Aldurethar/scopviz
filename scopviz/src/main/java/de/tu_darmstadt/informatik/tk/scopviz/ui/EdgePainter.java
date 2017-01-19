package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Collection;
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

	private boolean showEdges = true;

	private Color color = Color.BLACK;
	private boolean antiAlias = true;

	private HashSet<Edge> edges;

	private Boolean showWeights = true;

	/**
	 * @param track
	 *            the track
	 */
	public EdgePainter(Collection<Edge> track) {
		// copy the list so that changes in the
		// original list do not have an effect here
		this.edges = new HashSet<Edge>(track);
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {

		if (showEdges) {
			g = (Graphics2D) g.create();

			// convert from viewport to world bitmap
			Rectangle rect = map.getViewportBounds();
			g.translate(-rect.x, -rect.y);

			if (antiAlias)
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// do the drawing
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(4));

			drawRoute(g, map);

			// do the drawing again
			g.setColor(color);
			g.setStroke(new BasicStroke(2));

			drawRoute(g, map);

			g.dispose();
		}
	}

	/**
	 * Draws a line on the street map for all edges
	 * 
	 * @param g
	 *            the graphics object
	 * @param map
	 *            the map
	 */
	private void drawRoute(Graphics2D g, JXMapViewer map) {

		for (Edge edge : edges) {

			// Get geo Positions of the two nodes that define the edge
			GeoPosition startPos = new GeoPosition(edge.getNode0().getAttribute("lat"),
					edge.getNode0().getAttribute("long"));
			GeoPosition endPos = new GeoPosition(edge.getNode1().getAttribute("lat"),
					edge.getNode1().getAttribute("long"));

			// convert geo-coordinate to world bitmap pixel
			Point2D startPoint = map.getTileFactory().geoToPixel(startPos, map.getZoom());
			Point2D endPoint = map.getTileFactory().geoToPixel(endPos, map.getZoom());

			g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) endPoint.getX(), (int) endPoint.getY());

			if (showWeights) {

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
		}
	}

	/**
	 * Sets the removeEdges attribute
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
