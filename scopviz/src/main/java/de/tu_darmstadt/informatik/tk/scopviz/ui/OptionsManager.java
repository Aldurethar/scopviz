package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;
import java.util.Optional;

import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
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
public class OptionsManager {
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

	// settings
	private static int defaultWeight = 0;
	private static boolean showWeight = true;
	private static String nodeGraphics = null;

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

		Optional<ArrayList<String>> result = addPropDialog.showAndWait();

	}

	public static void adjustNodeGraphics(String newGraphics) {
		if (!newGraphics.equalsIgnoreCase(nodeGraphics)) {
			nodeGraphics = newGraphics;
			GraphManager g = Main.getInstance().getGraphManager();
			g.getGraph().removeAttribute("ui.stylesheet");
			g.getGraph().addAttribute("ui.stylesheet", g.getStylesheet());

			if (newGraphics.equals(allNodeGraphics[0])) {
				g.getGraph().addAttribute("ui.stylesheet", STYLE_NODES_SHAPES);
			} else if (newGraphics.equals(allNodeGraphics[1])) {
				g.getGraph().addAttribute("ui.stylesheet", STYLE_NODES_SPRITES);
			} else {
				throw new RuntimeException("These graphics do not exist");
			}
		}
	}

	/**
	 * @return the allNodeGraphics
	 */
	public static String[] getAllNodeGraphics() {
		return allNodeGraphics;
	}

	/**
	 * @return the defaultWeight
	 */
	public static int getDefaultWeight() {
		return defaultWeight;
	}

	/**
	 * @param defaultWeight
	 *            the defaultWeight to set
	 */
	public static void setDefaultWeight(int defaultWeight) {
		OptionsManager.defaultWeight = defaultWeight;
	}

	/**
	 * @return the showWeight
	 */
	public static boolean isWeightShown() {
		return showWeight;
	}

	/**
	 * @param showWeight
	 *            the showWeight to set
	 */
	public static void setShowWeight(boolean showWeight) {
		OptionsManager.showWeight = showWeight;
	}

	/**
	 * @return the nodeGraphics
	 */
	public static String getNodeGraphics() {
		return nodeGraphics;
	}

	/**
	 * @param nodeGraphics
	 *            the nodeGraphics to set
	 */
	public static void setNodeGraphics(String nodeGraphics) {
		OptionsManager.nodeGraphics = nodeGraphics;
	}

}
