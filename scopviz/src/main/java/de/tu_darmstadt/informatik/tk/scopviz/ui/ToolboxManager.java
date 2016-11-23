package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class ToolboxManager {
	public static void initialize(ListView<String> toolbox){

		ObservableList<String> dataToolbox = FXCollections.observableArrayList("toolbox");
		toolbox.setItems(dataToolbox);
	}
}
