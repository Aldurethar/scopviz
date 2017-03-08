package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;

/**
 * Class to manage the various Stylesheets used by the Graph and UI Elements.
 * 
 * @author Jascha Bohne
 *
 */
public class StylesheetManager {

	/**
	 * all available graphic styles
	 */
	private static String[] allNodeGraphics = { "Shapes", "Sprites" };
	/**
	 * The Stylesheet that is given to every graph that is added to display
	 * everything correctly.
	 */
	public static final String DEFAULT_STYLESHEET = "node{text-alignment:at-right;} \n"
			+ "edge{text-offset: 4px,-4px;} edge.selected{fill-color: #FF0000;}";
	/**
	 * Part of the stylesheet that styles the different Nodes with shapes.
	 */
	public static final String STYLE_NODES_SHAPES = "node.standard{shape: circle;}" + "node.source{shape: rounded-box;}"
			+ "node.procEn{shape: diamond;}" + "node.sink{shape: cross;}";
	/**
	 * Part of the stylesheet that styles the different Nodes with sprites.
	 */
	public static final String STYLE_NODES_SPRITES = "node.standard{fill-mode: image-scaled; fill-image: url('src/main/resources/png/standard.png'); }"
			+ "node.source{fill-mode: image-scaled; fill-image: url('src/main/resources/png/source.png'); }"
			+ "node.procEn{fill-mode: image-scaled; fill-image: url('src/main/resources/png/procEn.png'); }"
			+ "node.sink{fill-mode: image-scaled; fill-image: url('src/main/resources/png/sink.png'); }"
			+ "node.operator{fill-mode: image-scaled; fill-image: url('src/main/resources/png/operator.png'); }";

	/** The currently selected Display Mode. */
	private static String nodeGraphics = allNodeGraphics[1];
	/** The currently active Stylesheet. */
	private static String nodeStylesheet = STYLE_NODES_SPRITES;

	/** Layer specific Stylesheet for Underlay layer. */
	private static String styleLayerUnderlay = "";
	/** Layer specific Stylesheet for Operator layer. */
	private static String styleLayerOperator = "";
	/** Layer specific Stylesheet for Mapping layer. */
	private static String styleLayerMapping = "edge.mapping {stroke-color: #33ff33; stroke-mode: dashes; fill-mode: none; size: 0px;}"
			+ "node.procEn {fill-mode: plain; shape: pie-chart; fill-color: #555555, #cccc00, #32cd32, #8b0000; size: 20px;}";
	/** Layer specific Stylesheet for Symbol layer. */
	private static String styleLayerSymbol = "";

	/** Private Constructor to prevent instantiation. */
	private StylesheetManager() {
	}

	/**
	 * Changes the Stylesheet and updates all Nodes to use it.
	 * 
	 * @param newGraphics
	 *            the new Stylesheet to use
	 */
	public static void adjustNodeGraphics(String newGraphics) {
		if (!newGraphics.equalsIgnoreCase(StylesheetManager.nodeGraphics)) {
			StylesheetManager.nodeGraphics = newGraphics;
			if (newGraphics.equals(StylesheetManager.allNodeGraphics[0])) {
				StylesheetManager.setNodeGraphics(StylesheetManager.STYLE_NODES_SHAPES);
			} else if (newGraphics.equals(StylesheetManager.allNodeGraphics[1])) {
				StylesheetManager.setNodeGraphics(StylesheetManager.STYLE_NODES_SPRITES);
			} else {
				throw new RuntimeException("These graphics do not exist");
			}
		}
		Main.getInstance().getGraphManager().updateStylesheet();
	}

	/**
	 * Returns all available Stylesheets as Strings.
	 * 
	 * @return all the StyleSheets
	 */
	public static String[] getAllNodeGraphics() {
		return StylesheetManager.allNodeGraphics;
	}

	/**
	 * Returns the currently active StyleSheet.
	 * 
	 * @return the currently active StyleSheet as a String
	 */
	public static String getNodeGraphics() {
		return StylesheetManager.nodeStylesheet;
	}

	/**
	 * Sets the current Stylesheet.
	 * 
	 * @param nodeGraphics
	 *            the Stylesheet to use
	 */
	public static void setNodeGraphics(String nodeGraphics) {
		StylesheetManager.nodeStylesheet = nodeGraphics;
	}

	/**
	 * Returns the styleSheet for a given Layer.
	 * 
	 * @param l
	 *            the Layer
	 * @return the Stylesheet
	 */
	public static String getLayerStyle(Layer l) {
		switch (l) {
		case UNDERLAY:
			return styleLayerUnderlay;
		case OPERATOR:
			return styleLayerOperator;
		case MAPPING:
			return styleLayerMapping;
		case SYMBOL:
			return styleLayerSymbol;
		default:
			Debug.out("OptionsManager: Stylesheet for an unknown Layer Requested");
			return "";
		}
	}

	/**
	 * Sets the Stylesheet for a given Layer.
	 * 
	 * @param l
	 *            the Layer
	 * @param newStyle
	 *            the Stylesheet
	 */
	public static void setLayerStyle(Layer l, String newStyle) {
		switch (l) {
		case UNDERLAY:
			styleLayerUnderlay = newStyle;
			break;
		case OPERATOR:
			styleLayerOperator = newStyle;
			break;
		case MAPPING:
			styleLayerMapping = newStyle;
			break;
		case SYMBOL:
			styleLayerSymbol = newStyle;
			break;
		default:
			Debug.out("OptionsManager: Stylesheet for an unknown Layer Requested");
		}
	}

}
