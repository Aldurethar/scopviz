package de.tu_darmstadt.informatik.tk.scopviz.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 * Manager for the metric box 
 * 
 * @author Julian Ohl
 * @version 1.0.0.0
 */
public class MetricboxManager {
	//Test Pair of type <String,String>
	static Pair<String,String> testPair = new Pair<String,String>("Hi","test");
	static GUIController controller;
	
	/**
	 * Initialize metricbox by setting controller and showing all metrics
	 * 
	 * @param guiController 
	 */
	public static void initialize(GUIController guiController) {
		controller = guiController;
		showMetric();
	}
	
	/**
	 * displays all metrics in the metric box by setting the items for the metric table view (metricbox)
	 */
	public static void showMetric(){
		ObservableList<Pair<String, String>> newData = FXCollections.observableArrayList();
		newData.add(testPair);
		
		controller.metricbox.setItems(newData);
	}
}
