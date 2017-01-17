package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * manages the settings of the program also stores the constants
 * 
 * @author jascha-b
 * @version 1.0.0.0
 */
public final class OptionsManager {
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
	public static final String STYLE_NODES_SPRITES = "node.standard{fill-mode: image-scaled; fill-image: url('src/main/resources/png/node.png'); }"
			+ "node.source{fill-mode: image-scaled; fill-image: url('src/main/resources/png/source.png'); }"
			+ "node.procEn{fill-mode: image-scaled; fill-image: url('src/main/resources/png/enProc.png'); }"
			+ "node.sink{fill-mode: image-scaled; fill-image: url('src/main/resources/png/sink.png'); }";

	// SETTINGS
	/** The Default Weight for all new Edges. */
	private static int defaultWeight = 0;
	/** Flag whether to show the weight labels on Edges. */
	private static boolean showWeight = true;
	/** The currently selected Display Mode */
	private static String nodeGraphics = allNodeGraphics[1];
	/** The currently active Stylesheet. */
	private static String nodeStylesheet = null;

	// Layer stylesheets
	private static String styleLayerUnderlay = "";
	private static String styleLayerOperator = "";
	private static String styleLayerMapping = "";
	private static String styleLayerSymbol = "";

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private OptionsManager() {
	}

	/**
	 * opens a dialog that can be used to edit options
	 */
	public static void openOptionsDialog() {
		// Create new Dialog
		Dialog<ArrayList<String>> addPropDialog = new Dialog<>();
		addPropDialog.setTitle("Preferences");

		ButtonType addButtonType = new ButtonType("save & exit", ButtonData.OK_DONE);
		addPropDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// create grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// create dialog elements
		TextField defaultWeightField = new TextField();
		defaultWeightField.setPromptText(Integer.toString(defaultWeight));

		RadioButton showWeightButton = new RadioButton();
		showWeightButton.setSelected(showWeight);

		ChoiceBox<String> nodeGraphicsSelector = new ChoiceBox<String>();
		nodeGraphicsSelector.setItems(FXCollections.observableArrayList(allNodeGraphics[0], allNodeGraphics[1]));
		nodeGraphicsSelector.getSelectionModel().select(nodeGraphics);
		;

		// position elements on grid
		grid.add(new Label("Default weight of edges:"), 0, 0);
		grid.add(defaultWeightField, 1, 0);
		grid.add(new Label("Show weight of edges in the eraph viewer"), 0, 1);
		grid.add(showWeightButton, 1, 1);
		grid.add(new Label("Node display:"), 0, 2);
		grid.add(nodeGraphicsSelector, 1, 2);

		// set dialog
		addPropDialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> defaultWeightField.requestFocus());

		// get new property values
		addPropDialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				try {
					defaultWeight = Integer.parseInt(defaultWeightField.getText());
				} catch (NumberFormatException e) {
				}
				showWeight = showWeightButton.isSelected();
				adjustNodeGraphics(nodeGraphicsSelector.getValue());
				return null;
			} else
				return null;

		});
		addPropDialog.showAndWait();

	}

	/**
	 * Changes the Stylesheet and updates all Nodes to use it.
	 * 
	 * @param newGraphics
	 *            the new Stylesheet to use
	 */
	public static void adjustNodeGraphics(String newGraphics) {
		if (!newGraphics.equalsIgnoreCase(nodeGraphics)) {
			if (newGraphics.equals(allNodeGraphics[0])) {
				setNodeGraphics(STYLE_NODES_SHAPES);
			} else if (newGraphics.equals(allNodeGraphics[1])) {
				setNodeGraphics(STYLE_NODES_SPRITES);
			} else {
				throw new RuntimeException("These graphics do not exist");
			}
		}
	}

	/**
	 * Returns all available Stylesheets as Strings.
	 * 
	 * @return all the StyleSheets
	 */
	public static String[] getAllNodeGraphics() {
		return allNodeGraphics;
	}

	/**
	 * Returns the default weight for new Edges.
	 * 
	 * @return the default weight
	 */
	public static int getDefaultWeight() {
		return defaultWeight;
	}

	/**
	 * Sets the default weight for new Edges.
	 * 
	 * @param defaultWeight
	 *            the defaultWeight to set
	 */
	public static void setDefaultWeight(int defaultWeight) {
		OptionsManager.defaultWeight = defaultWeight;
	}

	/**
	 * Returns whether Edge weight should be displayed as labels.
	 * 
	 * @return true if weight should be shown, false otherwise
	 */
	public static boolean isWeightShown() {
		return showWeight;
	}

	/**
	 * Sets the Flag whether Edge weight should be displayed as labels.
	 * 
	 * @param showWeight
	 *            the showWeight to set
	 */
	public static void setShowWeight(boolean showWeight) {
		OptionsManager.showWeight = showWeight;
	}

	/**
	 * Returns the currently active StyleSheet.
	 * 
	 * @return the currently active StyleSheet as a String
	 */
	public static String getNodeGraphics() {
		return nodeStylesheet;
	}

	/**
	 * Sets the current Stylesheet.
	 * 
	 * @param nodeGraphics
	 *            the Stylesheet to use
	 */
	public static void setNodeGraphics(String nodeGraphics) {
		OptionsManager.nodeStylesheet = nodeGraphics;
	}

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
