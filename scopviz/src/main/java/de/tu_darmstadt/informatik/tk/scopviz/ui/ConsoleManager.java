package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * 
 * @author Julian Ohl
 * @version 1.0
 */
public class ConsoleManager {

	/**
	 * Reference to the GUI Controller for Access to various GUI Elements.
	 */
	private static GUIController controller;

	/**
	 * Initialize console window by setting controller
	 * 
	 * @param c
	 *            our GUIController
	 */
	public static void initialize(GUIController c) {
		controller = c;

		// behaviour for autoscrolling of the console window
		controller.consoleWindow.getChildren().addListener((ListChangeListener<Node>) ((change) -> {
			controller.consoleWindow.layout();
			controller.consoleScrollPane.layout();
			controller.consoleScrollPane.setVvalue(1.0f);
		}));
	}

	/**
	 * Add normal text to the console output
	 * 
	 * @param s
	 *            the text, which should be displayed
	 */
	public static void addNormalText(String s) {
		StringBuilder sb = new StringBuilder();

		sb.append(System.lineSeparator()).append("> ").append(s);

		controller.consoleWindow.getChildren().add(new Text(sb.toString()));
	}

	/**
	 * Add error text to the console output
	 * 
	 * @param s
	 *            the text, which should be displayed
	 */
	public static void addErrorText(String s) {
		StringBuilder sb = new StringBuilder();

		sb.append(System.lineSeparator()).append("> ").append(s);

		Text errorText = new Text(sb.toString());

		errorText.setFill(Color.RED);
		controller.consoleWindow.getChildren().add(errorText);
	}
}
