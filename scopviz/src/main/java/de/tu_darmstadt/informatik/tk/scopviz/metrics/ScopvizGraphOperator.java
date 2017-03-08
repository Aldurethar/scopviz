package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.main.MyGraph;

public interface ScopvizGraphOperator {

	/**
	 * calculates a new Version of the Graph using the given operator.
	 * 
	 * @param g
	 *            a MyGraph
	 * @return a list of Graphs that is the result of the operator on the Graph
	 *         g
	 */
	public LinkedList<MyGraph> calculate(MyGraph g);

	/**
	 * returns the name of the Metric.
	 */
	public String getName();

	/**
	 * sets up the metric for the first use.
	 */
	public void setup();
}
