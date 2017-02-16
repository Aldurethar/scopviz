package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.LinkedList;

import javafx.util.Pair;

public interface ScopvizGraphMetric {

	/**
	 * calculate the metric on the graph
	 * 
	 * @param g
	 *            a MyGraph
	 * @return a pair that is displayed in the metrics window
	 */
	public LinkedList<Pair<String, String>> calculate(MyGraph g);

	/**
	 * returns the name of the Metric which will be displayed above the values
	 */
	public Pair<String, Object> getName();
}
