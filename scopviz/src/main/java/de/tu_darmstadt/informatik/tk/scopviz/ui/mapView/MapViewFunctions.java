package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;

public final class MapViewFunctions {

	private static final Log log = LogFactory.getLog(MapViewFunctions.class);

	/**
	 * Hash map to save, scaled images
	 */
	public static HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();

	/**
	 * the selected mapType "Default", "Road", "Satellite", "Hybrid"
	 */
	private static String mapType = "Default";

	/**
	 * private constructor to avoid instantiation
	 */
	private MapViewFunctions() {

	}

	/**
	 * resets the hash map with the pictures
	 */
	public static void resetImageMap() {
		imageMap = new HashMap<String, BufferedImage>();
	}

	/**
	 * load and scale waypoint images and save them in a HashMap
	 */
	public static void initializeWaypointImages() {

		if (WorldView.getWaypoints() == null) {
			return;
		}

		imageMap = new HashMap<String, BufferedImage>(WorldView.getWaypoints().size());

		for (CustomWaypoint w : WorldView.getWaypoints()) {

			BufferedImage origImage = null;

			// image not loaded
			if (!imageMap.containsKey(w.getDeviceType())) {

				// try load image
				try {
					origImage = ImageIO.read(w.getResource());

				} catch (Exception ex) {
					log.warn("couldn't read Waypoint png", ex);
				}

				// loading complete
				if (origImage != null) {
					// scale image down
					BufferedImage myImg = MapViewFunctions.scaleImage(origImage, CustomWaypointRenderer.SCALEWIDTH,
							CustomWaypointRenderer.SCALEHEIGHT);

					// save image in hash map
					imageMap.put(w.getDeviceType(), myImg);
				}
			}

		}

	}

	/**
	 * Scale a given BufferedImage down to given width w and given height h
	 * 
	 * @param loadImg
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage loadImg, int w, int h) {
		BufferedImage imgOut = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = imgOut.createGraphics();
		graphics.drawImage(loadImg, 0, 0, w, h, null);
		graphics.dispose();

		return imgOut;
	}

	/**
	 * change all pixels from a given color to another given color (under a
	 * given alpha value)
	 *
	 * @param image
	 * @param toChange
	 *            change all pixels with this color to another color
	 * @param changeWith
	 *            new color
	 * @param alpha
	 * @return
	 */
	public static BufferedImage colorImage(BufferedImage image, Color toChange, Color changeWith, int alpha) {
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage imgOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				Color originalColor = new Color(image.getRGB(xx, yy), true);

				// pixel needs to be changed
				if (originalColor.equals(toChange) && originalColor.getAlpha() == alpha) {
					imgOut.setRGB(xx, yy, changeWith.getRGB());
				}
			}
		}
		return imgOut;
	}

	/**
	 * Returns either an EdgePainter (case input "edge") or a WaypointPainter
	 * (case input "waypoint") based on input
	 * 
	 * @param mode
	 *            0 or 1
	 * @return EdgePainter or WaypointPainter if existing in CompoundPainter
	 *         otherwise null
	 */
	private static Painter<JXMapViewer> getRequestedPainter(String requested) {

		// return value
		switch (requested) {
		case "edge":
			return WorldView.edgePainter;
		case "waypoint":
			return WorldView.waypointPainter;
		default:
			return null;

		}

	}

	/**
	 * change the shown map based on the given string
	 * 
	 * @param string
	 */
	public static void changeMapView(String selected) {

		mapType = selected;

		switch (selected) {
		case "Default":
			TileFactoryInfo defaultTileFactoryInfo = new OSMTileFactoryInfo();
			WorldView.internMapViewer.setTileFactory(new CustomTileFactory(defaultTileFactoryInfo));
			break;

		case "Road":
			TileFactoryInfo roadTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
			WorldView.internMapViewer.setTileFactory(new CustomTileFactory(roadTileFactoryInfo));
			break;

		case "Satellite":
			TileFactoryInfo sateliteTileFactoryInfo = new VirtualEarthTileFactoryInfo(
					VirtualEarthTileFactoryInfo.SATELLITE);
			WorldView.internMapViewer.setTileFactory(new CustomTileFactory(sateliteTileFactoryInfo));
			break;

		case "Hybrid":
			TileFactoryInfo hybridTileFactoryInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
			WorldView.internMapViewer.setTileFactory(new CustomTileFactory(hybridTileFactoryInfo));
			break;
		}
	}

	/**
	 * Check if Checkboxes or mapType where changed, last time symbol-rep. layer
	 * was shown
	 */
	public static void checkVBoxChanged() {

		EdgePainter edgePainter = (EdgePainter) getRequestedPainter("edge");
		WaypointPainter<CustomWaypoint> waypointPainter = (WaypointPainter<CustomWaypoint>) getRequestedPainter(
				"waypoint");

		// Checkboxes were changed last time symbolRep-Layer was shown
		if (!WorldView.controller.edgesVisibleCheckbox.isSelected()) {
			edgePainter.setShowEdges(false);
		}
		if (!WorldView.controller.nodeLabelCheckbox.isSelected()) {
			CustomWaypointRenderer renderer = new CustomWaypointRenderer();
			renderer.setShowLabels(false);
			waypointPainter.setRenderer(renderer);
		}
		if (!WorldView.controller.edgeWeightCheckbox.isSelected()) {
			edgePainter.setShowWeights(false);
		}
		if (!mapType.equals("Default")) {
			changeMapView(mapType);
		}
	}

	/**
	 * Show a ContextMenu when map was right clicked to change symbol layer
	 * checkbox properties
	 * 
	 * @param event
	 *            contextMenu mouse event
	 */
	public static void contextMenuRequest(ContextMenuEvent event) {

		// Declare context menu and items
		final ContextMenu menu = new ContextMenu();
		final MenuItem edgeVisible = new MenuItem("Hide Edges");
		final MenuItem weightVisible = new MenuItem("Hide Weights");
		final MenuItem labelVisible = new MenuItem("Hide Labels");

		// the checkboxes in dhe symbol layer
		CheckBox edgeCheckbox = WorldView.controller.edgesVisibleCheckbox;
		CheckBox weightCheckbox = WorldView.controller.edgeWeightCheckbox;
		CheckBox labelCheckbox = WorldView.controller.nodeLabelCheckbox;

		// define the actions when clicked on menu item
		edgeVisible.setOnAction((actionEvent) -> {
			if (edgeCheckbox.isSelected()) {
				edgeCheckbox.setSelected(false);
			} else {
				edgeCheckbox.setSelected(true);
			}
		});

		weightVisible.setOnAction((actionEvent) -> {
			if (weightCheckbox.isSelected()) {
				weightCheckbox.setSelected(false);
			} else {
				weightCheckbox.setSelected(true);
			}
		});

		labelVisible.setOnAction((actionEvent) -> {
			if (labelCheckbox.isSelected()) {
				labelCheckbox.setSelected(false);
			} else {
				labelCheckbox.setSelected(true);
			}
		});

		// bind the text properties to the menu item, so that they change
		// depending on the selection property
		edgeVisible.textProperty()
				.bind(Bindings.when(edgeCheckbox.selectedProperty()).then("Hide Edges").otherwise("Show Edges"));

		weightVisible.textProperty()
				.bind(Bindings.when(weightCheckbox.selectedProperty()).then("Hide Weights").otherwise("Show Weights"));

		labelVisible.textProperty()
				.bind(Bindings.when(labelCheckbox.selectedProperty()).then("Hide Labels").otherwise("Show Labels"));

		menu.getItems().addAll(edgeVisible, weightVisible, labelVisible);

		// show context menu at the clicked point
		menu.show(Main.getInstance().getPrimaryStage(), event.getScreenX(), event.getScreenY());

	}

	/**
	 * switch to previous Waypoint
	 */
	public static void switchToPreviousWaypoint() {

		CustomWaypoint selectedWaypoint = CustomMapClickListener.selectedNode;

		if (selectedWaypoint == null && WorldView.getWaypointsAsArrayList().size() > 0) {
			CustomMapClickListener.selectWaypoint(WorldView.getWaypointsAsArrayList().get(0));

		} else {
			int index = WorldView.getWaypointsAsArrayList().indexOf(selectedWaypoint);

			if (index == 0) {
				CustomMapClickListener.selectWaypoint(
						WorldView.getWaypointsAsArrayList().get(WorldView.getWaypointsAsArrayList().size() - 1));
			} else {
				CustomMapClickListener.selectWaypoint(WorldView.getWaypointsAsArrayList().get(index - 1));
			}
		}
	}

	/**
	 * switch to next Waypoint
	 */
	public static void switchToNextWaypoint() {

		CustomWaypoint selectedWaypoint = CustomMapClickListener.selectedNode;

		if (selectedWaypoint == null && WorldView.getWaypointsAsArrayList().size() > 0) {
			CustomMapClickListener.selectWaypoint(WorldView.getWaypointsAsArrayList().get(0));

		} else {
			int index = WorldView.getWaypointsAsArrayList().indexOf(selectedWaypoint);

			if (index == WorldView.getWaypointsAsArrayList().size() - 1) {
				CustomMapClickListener.selectWaypoint(WorldView.getWaypointsAsArrayList().get(0));
			} else {
				CustomMapClickListener.selectWaypoint(WorldView.getWaypointsAsArrayList().get(index + 1));
			}
		}

	}

}
