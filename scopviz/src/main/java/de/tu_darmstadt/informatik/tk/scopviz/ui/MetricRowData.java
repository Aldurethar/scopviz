package de.tu_darmstadt.informatik.tk.scopviz.ui;

import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

/**
 * Wrapper for a metric to make it usable as an item for the metricbox table
 * view.
 * 
 * 
 * @author Julian Ohl
 * @version 1.2.0.0
 */
public class MetricRowData {

	// the SimpleStringProperties shown in a row of the metric properties
	private SimpleStringProperty metricName;
	private SimpleStringProperty value;
	private SimpleBooleanProperty checked;

	// the wrapped metric of this MetricRowData
	private ScopvizGraphMetric metric;

	/**
	 * sets the name of the metric, doesn't calculate a value until an update,
	 * unchecked as initial state for checkbox
	 * 
	 * @param metric
	 *            the wrapped metric
	 */
	public MetricRowData(ScopvizGraphMetric metric) {
		this.metric = metric;
		this.metricName = new SimpleStringProperty(metric.getName());
		this.value = new SimpleStringProperty();
		this.checked = new SimpleBooleanProperty(false);
	}

	/**
	 * 
	 * @param g
	 *            the current mapping graph, used for calculating the metric
	 */
	public void updateMetric(MyGraph g) {

		metricName = new SimpleStringProperty(buildMetricName(g));
		value = new SimpleStringProperty(buildMetricValues(g));

	}

	/**
	 * 
	 * @param g
	 *            the current mapping graph, used for calculating the metric
	 * @return a string with the name of the metric in the first line and names
	 *         of the different graphs associated with the values in lines one
	 *         below the other
	 */
	private String buildMetricName(MyGraph g) {
		LinkedList<Pair<String, String>> list = metric.calculate(g);

		StringBuilder sb = new StringBuilder();
		sb.append(metric.getName());

		for (Pair<String, String> p : list) {
			sb.append(System.lineSeparator()).append("\t").append(p.getKey());
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param g
	 *            the current mapping graph, used for calculating the metric
	 * @return a string with the individual values of the metric one below the
	 *         other in lines
	 */
	private String buildMetricValues(MyGraph g) {
		LinkedList<Pair<String, String>> list = metric.calculate(g);

		StringBuilder sb = new StringBuilder();

		for (Pair<String, String> p : list) {
			sb.append(System.lineSeparator()).append(p.getValue());
		}
		return sb.toString();
	}

	// all getters

	public String getMetricName() {
		return metricName.get();
	}

	public String getValue() {
		return value.get();
	}

	public boolean getChecked() {

		return checked.get();
	}

	public SimpleBooleanProperty checkedProperty() {
		return checked;
	}

	/**
	 * 
	 * @return the wrapped metric
	 */
	public ScopvizGraphMetric getMetric() {

		return metric;
	}

}
