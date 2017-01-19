package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.net.URL;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A waypoint that also has a color and a label
 * 
 * @author Martin Steiger
 */
public class CustomWaypoint extends DefaultWaypoint {

	private final String label;
	private final URL resource;

	/**
	 * @param label
	 *            the text
	 * @param color
	 *            the color
	 * @param coord
	 *            the coordinate
	 */
	public CustomWaypoint(String label, URL resource, GeoPosition coord) {
		super(coord);
		this.label = label;
		this.resource = resource;
	}

	/**
	 * @return the label text
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the ressource png
	 */
	public URL getResource() {
		return resource;
	}
}
