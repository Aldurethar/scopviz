package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Manager for the Properties pane and its contents.
 * 
 * @author Julian Ohl
 * @version 1.0
 *
 */
public class PropertiesManager {

	/**
	 * Initializes the Manager by adding the List of properties to display into
	 * the properties pane.
	 * 
	 * @param properties
	 *            The list of properties to display
	 */
	public static void initialize(ListView<String> properties) {
		ObservableList<String> dataProperties = FXCollections.observableArrayList("CPU", "OPS");
		properties.setItems(dataProperties);

		properties.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> properties) {
				return new PropertiesManager.LabelCell();
			}
		});
	}

	/**
	 * Internal Class to represent a Cell containing a label. Needed for factory
	 * pattern.
	 * 
	 * @author Julian Ohl
	 * @version 1.0
	 *
	 */
	static class LabelCell extends ListCell<String> {

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				if (item.equals("CPU")) {
					setGraphic(new TextField(item));
				}
				if (item.equals("OPS")) {
					setGraphic(new Label(item));
				}
			}
		}
	}
}
