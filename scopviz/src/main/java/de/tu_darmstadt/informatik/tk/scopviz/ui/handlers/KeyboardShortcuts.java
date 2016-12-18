package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

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
public class KeyboardShortcuts {

	// example of keycombinations
	final KeyCombination rCtrl = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
	final KeyCombination rCtrlShift = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN,
			KeyCombination.SHIFT_DOWN);

	public KeyboardShortcuts(Stage primaryStage) {

		primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, buttonsPressed);

	}

	// a general EventHandler for all key combinations
	private EventHandler<KeyEvent> buttonsPressed = new EventHandler<KeyEvent>() {

		@Override
		public void handle(KeyEvent event) {

			if (rCtrl.match(event)) {
				System.out.println("Ctrl+R pressed");
			}

			else if (rCtrlShift.match(event)) {
				System.out.println("Ctrl+Shift+R pressed");
			}

		}
	};

}
