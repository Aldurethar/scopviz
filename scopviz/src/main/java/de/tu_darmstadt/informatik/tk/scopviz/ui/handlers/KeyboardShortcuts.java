package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Class for defining keyboard shortcuts for all kind of actions. Shortcuts for
 * actions in the menu bar should be defined in the fxml file as accelerators.
 * 
 * @author Julian Ohl (julian.ohl95@web.de)
 * @version 1.0
 *
 */
public final class KeyboardShortcuts {

	// example of keycombinations
	final static KeyCombination mShift = new KeyCodeCombination(KeyCode.M, KeyCombination.SHIFT_DOWN);
	final static KeyCombination rAltShift = new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN,
			KeyCombination.SHIFT_DOWN);

	/**
	 * Private constructor to prevent Instantiation.
	 */
	private KeyboardShortcuts() {
	}

	/**
	 * Initialize the Keyboard Shortcuts, add them to the Stage.
	 * 
	 * @param primaryStage
	 *            the Stage
	 */
	public static void initialize(Stage primaryStage) {

		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, buttonsPressed);
		primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, buttonsReleased);

	}

	/**
	 * A general Handler for any Button presses.
	 */
	private static EventHandler<KeyEvent> buttonsPressed = new EventHandler<KeyEvent>() {

		@Override
		public void handle(KeyEvent event) {

			if (event.getCode() == KeyCode.CONTROL) {

				// for functionality of holding down ctrl for creating mapping
				// edges
				if (Main.getInstance().getGraphManager().getGraph().getAttribute("layer") == Layer.MAPPING) {
					Main.getInstance().setCreationMode(CreationMode.CREATE_DIRECTED_EDGE);
					Debug.out("Ctrl pressed");
				}

			}

		}
	};

	/**
	 * A general Handler for any Button releases
	 */
	private static EventHandler<KeyEvent> buttonsReleased = new EventHandler<KeyEvent>() {

		@Override
		public void handle(KeyEvent event) {

			if (mShift.match(event)) {
				Debug.out("M+Shift released");
			}

			else if (rAltShift.match(event)) {
				Debug.out("Alt+Shift+R released");
			}

			else if (event.getCode() == KeyCode.CONTROL) {

				// for functionality of holding down ctrl for creating mapping
				// edges
				if (Main.getInstance().getGraphManager().getGraph().getAttribute("layer") == Layer.MAPPING) {
					Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
					Main.getInstance().getGraphManager().deselectEdgeCreationNodes();
					Debug.out("Ctrl released");
				}

			}

		}
	};

}
