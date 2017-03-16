package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

public class CustomWaypointRenderer implements WaypointRenderer<CustomWaypoint> {

	/**
	 * The font in which labels are drawn
	 */
	private final Font font = new Font("Lucida Sans", Font.BOLD, 15);

	/**
	 * show labels property
	 */
	private Boolean showLabels = true;

	/**
	 * the standard background color of images
	 */
	public static final Color STANDARD = Color.BLACK;

	/**
	 * the color of an image, when it was clicked
	 */
	public static final Color CLICKED = Color.RED;

	/**
	 * an rgb alpha value for computing only
	 */
	public static final int ALPHA = 255;

	/**
	 * the standard width of the shown images, after scaling it
	 */
	public static final int SCALEWIDTH = 60;

	/**
	 * the standard height of the shwon images, after scaling it
	 */
	public static final int SCALEHEIGHT = 60;

	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer viewer, CustomWaypoint w) {

		g = (Graphics2D) g.create();

		// get pre loaded image
		BufferedImage loadedImg = MapViewFunctions.imageMap.get(w.getDeviceType());

		if (w.getIsSelected()) {
			loadedImg = MapViewFunctions.colorImage(loadedImg, CustomWaypointRenderer.STANDARD,
					CustomWaypointRenderer.CLICKED, CustomWaypointRenderer.ALPHA);
		}

		// get waypoint position
		Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

		int x = (int) point.getX();
		int y = (int) point.getY();

		// draw image on map
		g.drawImage(loadedImg, x - loadedImg.getWidth() / 2, y - loadedImg.getHeight(), null);

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
			g.drawString(label, x - loadedImg.getWidth() / 2 - tw - 5, y + th / 2 - loadedImg.getHeight() / 2);

			g.dispose();
		}
	}

	public void setShowLabels(Boolean showLabels) {
		this.showLabels = showLabels;
	}

}
