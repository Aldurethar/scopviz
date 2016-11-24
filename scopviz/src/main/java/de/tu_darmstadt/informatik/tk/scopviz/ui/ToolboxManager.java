package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Manager for the Toolbox pane.
 * 
 * @author Dominik Renkel
 * @version 0.9
 *
 */
public class ToolboxManager {

	/**
	 * Initializes the toolbox to contain the specified list of entries.
	 * 
	 * @param toolbox
	 *            the list of entries to add to the toolbox
	 */
	public static void initialize(ListView<String> toolbox) {

		ObservableList<String> dataToolbox = FXCollections.observableArrayList("toolbox");
		toolbox.setItems(dataToolbox);
	}
}
