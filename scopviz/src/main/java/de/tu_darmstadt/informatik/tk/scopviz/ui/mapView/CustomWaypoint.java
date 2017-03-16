package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

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
	private final String nodeID;
	private final String deviceType;

	private Boolean isSelected = false;

	/**
	 * @param label
	 *            the text
	 * @param deviceType
	 * @param color
	 *            the color
	 * @param coord
	 *            the coordinate
	 */
	public CustomWaypoint(String label, String nodeID, URL resource, String deviceType, GeoPosition coord) {
		super(coord);
		this.label = label;
		this.resource = resource;
		this.deviceType = deviceType;
		this.nodeID = nodeID;
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

	/**
	 * 
	 * @return the id of the referenced node
	 */
	public String getNodeID() {
		return nodeID;
	}

	/**
	 * 
	 * @return the device type
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * change isSelected value to true
	 */
	public void select() {
		this.isSelected = true;
	}

	/**
	 * change isSelected value to false
	 */
	public void deselect() {
		this.isSelected = false;
	}

	/**
	 * return isSelected value
	 * 
	 * @return
	 */
	public Boolean getIsSelected() {
		return this.isSelected;
	}
}
