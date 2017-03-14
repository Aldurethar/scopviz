package de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces;

import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;

public interface ScopvizGraphOperator {

	/**
	 * Metric Returns true if the GraphOperator requires the Setup() to be
	 * called if this is false setup() will not be called.
	 */
	public boolean isSetupRequired();

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
