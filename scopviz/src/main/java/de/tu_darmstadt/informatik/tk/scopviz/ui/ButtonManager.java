package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.ArrayList;

import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Manager to contain the various handlers for the buttons of the UI.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public final class ButtonManager {

	/** Flag for creating more then one Edge at a time mode */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/** List of the Buttons for Layer switching */
	private static ArrayList<Button> layerButtons;

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private ButtonManager() {
	}

	/**
	 * Initializes the ButtonManager with a List of Buttons for Layer switching.
	 * 
	 * @param nList
	 *            the Layer switching Buttons
	 */
	public static void initialize(ArrayList<Button> nList) {
		layerButtons = nList;
	}

	/**
	 * Handler for zoom in Button
	 */
	public static final EventHandler<ActionEvent> zoomInHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getGraphManager().zoomIn();
		}
	};

	/**
	 * Handler for zoom out Button
	 */
	public static final EventHandler<ActionEvent> zoomOutHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent evt) {
			Main.getInstance().getGraphManager().zoomOut();
		}
	};

	/**
	 * Handler for the Underlay Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> underlayHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.UNDERLAY);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Operator Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> operatorHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.OPERATOR);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Mapping Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> mappingHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.MAPPING);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Handler for the Symbol Representation Layer switch Button.
	 */
	public static final EventHandler<ActionEvent> symbolRepHandler = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent arg0) {
			GraphDisplayManager.setCurrentLayer(Layer.SYMBOL);
			GraphDisplayManager.switchActiveGraph();

			setBorderStyle((Button) arg0.getSource());
		}

	};

	/**
	 * Changes the border of the button that was pressed to red
	 * 
	 * @param currentButton
	 *            the button that was pressed
	 */
	private static void setBorderStyle(Button currentButton) {

		for (Button j : layerButtons) {
			if (j.equals(currentButton)) {
				j.setStyle(
						"-fx-background-color: red, red, red, -fx-faint-focus-color, -fx-body-color; -fx-background-insets: -0.2, 1, 2, -1.4, 2.6; -fx-background-radius: 3, 2, 1, 4, 1;");
			} else {
				j.setStyle("-fx-border-width: 0;");
			}

		}
	}

}
