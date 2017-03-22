package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.CommunicationCostMetric;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.PlacementCostMetric;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.TestMetric;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manager for the metric box
 * 
 * @author Julian Ohl
 * @version 1.3.0.0
 */
public class MetricboxManager {

	private static GUIController controller;
	private static ObservableList<MetricRowData> metrics;

	/**
	 * Private Constructor to prevent Instantiation.
	 */
	private MetricboxManager() {
	}

	/**
	 * Initialize metricbox by setting controller, initializing all metrics and
	 * set them as items
	 * 
	 * @param guiController
	 */
	public static void initialize(GUIController guiController) {
		controller = guiController;
		initializeMetrics();
		controller.metricbox.setItems(metrics);
	}

	/**
	 * Initializes all metrics for employment
	 * 
	 * ****Central method to add a new metric***** Add line: metrics.add(new
	 * MetricRowData(new YourMetric())); for using it in the metricbox
	 * **************************************************************
	 * 
	 */
	private static void initializeMetrics() {
		metrics = FXCollections.observableArrayList();

		metrics.add(new MetricRowData(new TestMetric()));
		metrics.add(new MetricRowData(new PlacementCostMetric()));
		metrics.add(new MetricRowData(new CommunicationCostMetric()));
	}

	/**
	 * if in mapping layer: updates all values of all metrics that are checked
	 * and refreshes the metricbox
	 */
	public static void updateMetrics() {

		if (GraphDisplayManager.getCurrentLayer() == Layer.MAPPING) {

			for (MetricRowData d : metrics) {

				if (d.getChecked()) {

					d.updateMetric(Main.getInstance().getGraphManager().getGraph());

				}
			}
			controller.metricbox.refresh();
		}

	}

}
