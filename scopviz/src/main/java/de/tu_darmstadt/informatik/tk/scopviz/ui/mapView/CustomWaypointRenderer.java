package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

public class CustomWaypointRenderer implements WaypointRenderer<CustomWaypoint> {

	private static final Log log = LogFactory.getLog(CustomWaypointRenderer.class);

	private final Font font = new Font("Lucida Sans", Font.BOLD, 15);

	private Boolean showLabels = true;

	private static final Color STANDARD = Color.BLACK;

	private static final Color CLICKED = Color.RED;

	private static final int ALPHA = 255;

	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer viewer, CustomWaypoint w) {

		g = (Graphics2D) g.create();
		BufferedImage origImage = null;

		try {
			origImage = ImageIO.read(w.getResource());
			if (w.getIsSelected()) {
				origImage = MapViewFunctions.colorImage(origImage, STANDARD, CLICKED, ALPHA);
			}
		} catch (Exception ex) {
			log.warn("couldn't read Waypoint png", ex);
		}

		if (origImage == null)
			return;

		// scale image down
		BufferedImage myImg = MapViewFunctions.scaleImage(origImage, 60, 60);

		// get waypoint position
		Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

		int x = (int) point.getX();
		int y = (int) point.getY();

		g.drawImage(myImg, x - myImg.getWidth() / 2, y - myImg.getHeight(), null);

		if (showLabels) {

			// Set label Position on street map
			String label = w.getLabel();
			g.setFont(font);

			if (w.getIsSelected()) {
				g.setColor(CLICKED);
			} else
				g.setColor(STANDARD);

			// get label height and width under given font
			FontMetrics metrics = g.getFontMetrics();
			int tw = metrics.stringWidth(label);
			int th = 1 + metrics.getAscent();

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// Show label left middle of deviceType picture
			g.drawString(label, x - myImg.getWidth() / 2 - tw - 5, y + th / 2 - myImg.getHeight() / 2);

			g.dispose();
		}
	}

	public void setShowLabels(Boolean showLabels) {
		this.showLabels = showLabels;
	}

}