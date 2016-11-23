package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class PropertiesManager {

	public static void initialize(ListView<String> properties){
	ObservableList<String> dataProperties = FXCollections.observableArrayList("CPU", "OPS");
	properties.setItems(dataProperties);
	
	properties.setCellFactory(new Callback<ListView<String>, 
	            ListCell<String>>() {
	                @Override 
	                public ListCell<String> call(ListView<String> properties) {
	                    return new PropertiesManager.LabelCell();
	                }
	            }
	        );
	}
	static class LabelCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null){
	            if(item.equals("CPU")){
	            	setGraphic(new TextField(item));
	            }
	            if(item.equals("OPS")){
	            	setGraphic(new Label(item));
	            }
            }
        }
    }
}
