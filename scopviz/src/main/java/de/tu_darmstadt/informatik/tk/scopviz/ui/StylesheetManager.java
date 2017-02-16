package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;

public class StylesheetManager {

	/**
	 * all available graphic styles
	 */
	private static String[] allNodeGraphics = { "Shapes", "Sprites" };
	/**
	 * The Stylesheet that is given to every graph that is added to display
	 * everything correctly
	 */
	public static final String DEFAULT_STYLESHEET = "node{text-alignment:at-right;} \n"
			+ "edge{text-offset: 4px,-4px;}";
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
	/** The currently selected Display Mode */
	private static String nodeGraphics = allNodeGraphics[1];
	/** The currently active Stylesheet. */
	private static String nodeStylesheet = STYLE_NODES_SPRITES;

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
	 * Returns the styleSheet for a given Layer
	 * 
	 * @param l
	 *            the Layer
	 * @return the Stylesheet
	 */
	public static String getLayerStyle(Layer l) {
		switch (l) {
		case UNDERLAY:
			return OptionsManager.styleLayerUnderlay;
		case OPERATOR:
			return OptionsManager.styleLayerOperator;
		case MAPPING:
			return OptionsManager.styleLayerMapping;
		case SYMBOL:
			return OptionsManager.styleLayerSymbol;
		default:
			Debug.out("OptionsManager: Stylesheet for an unknown Layer Requested");
			return "";
		}
	}

	/**
	 * Sets the Stylesheet for a given Layer
	 * 
	 * @param l
	 *            the Layer
	 * @param newStyle
	 *            the Stylesheet
	 */
	public static void setLayerStyle(Layer l, String newStyle) {
		switch (l) {
		case UNDERLAY:
			OptionsManager.styleLayerUnderlay = newStyle;
			break;
		case OPERATOR:
			OptionsManager.styleLayerOperator = newStyle;
			break;
		case MAPPING:
			OptionsManager.styleLayerMapping = newStyle;
			break;
		case SYMBOL:
			OptionsManager.styleLayerSymbol = newStyle;
			break;
		default:
			Debug.out("OptionsManager: Stylesheet for an unknown Layer Requested");
		}
	}

}
