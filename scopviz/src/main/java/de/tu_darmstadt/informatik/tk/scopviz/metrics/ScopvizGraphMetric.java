package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;
import javafx.util.Pair;

public interface ScopvizGraphMetric {

	/**
	 * Returns true if the Metric requires the Setup() to be called if this is
	 * false setup() will not be called.
	 */
	public boolean isSetupRequired();

	/**
	 * calculate the metric on the graph.
	 * 
	 * @param g
	 *            a MyGraph
	 * @return a List of tableRows that will be displayed in the metrics window
	 */
	public LinkedList<Pair<String, String>> calculate(MyGraph g);

	/**
	 * returns the name of the Metric which will be displayed above the values.
	 */
	public String getName();

	/**
	 * sets up the metric for the first use.
	 */
	public void setup();
}
