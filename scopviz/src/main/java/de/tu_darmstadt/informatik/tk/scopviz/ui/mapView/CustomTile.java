package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import org.jxmapviewer.viewer.Tile;

/**
 * The Tile class represents a particular square image piece of the world bitmap
 * at a particular zoom level.
 * 
 * @author joshy
 */

public class CustomTile extends Tile {

	/**
	 * Indicates that loading has succeeded. A PropertyChangeEvent will be fired
	 * when the loading is completed
	 */

	private boolean loaded = false;

	private Priority priority = Priority.High;

	private CustomTileFactory dtf;

	private boolean isLoading = false;

	/**
	 * The url of the image to load for this tile
	 */
	private String url;

	/**
	 * The image loaded for this Tile
	 */
	SoftReference<BufferedImage> image = new SoftReference<BufferedImage>(null);

	/**
	 * Create a new Tile at the specified tile point and zoom level
	 * 
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 * @param zoom
	 *            the zoom level
	 */
	public CustomTile(int x, int y, int zoom) {
		super(x, y, zoom);
	}

	/**
	 * Create a new Tile that loads its data from the given URL. The URL must
	 * resolve to an image
	 * 
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 * @param zoom
	 *            the zoom level
	 * @param url
	 *            the URL
	 * @param priority
	 *            the priority
	 * @param dtf
	 *            the tile factory
	 */
	public CustomTile(int x, int y, int zoom, String url, Priority priority, CustomTileFactory dtf) {
		super(x, y, zoom);
		this.url = url;
		this.priority = priority;
		this.dtf = dtf;
	}

	/**
	 * @return the Image associated with this Tile. This is a read only property
	 *         This may return null at any time, however if this returns null, a
	 *         load operation will automatically be started for it.
	 */
	public BufferedImage getImage() {
		BufferedImage img = image.get();
		if (img == null) {
			setLoaded(false);

			// tile factory can be null if the tile has invalid coords or zoom
			if (dtf != null) {
				dtf.startLoading(this);
			}
		}

		return img;
	}

	/**
	 * Indicates if this tile's underlying image has been successfully loaded
	 * yet.
	 * 
	 * @return true if the Tile has been loaded
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}

	/**
	 * Toggles the loaded state, and fires the appropriate property change
	 * notification
	 * 
	 * @param loaded
	 *            the loaded flag
	 */
	public synchronized void setLoaded(boolean loaded) {
		boolean old = isLoaded();
		this.loaded = loaded;
		firePropertyChange("loaded", old, isLoaded());
	}

	/**
	 * @return the isLoading
	 */
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * @param isLoading
	 *            the isLoading to set
	 */
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	/**
	 * Gets the loading priority of this tile.
	 * 
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Set the loading priority of this tile.
	 * 
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Gets the URL of this tile.
	 * 
	 * @return the url
	 */
	public String getURL() {
		return url;
	}

}
