package de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;

public interface ScopvizGraphOperator {

	/**
	 * calculates a new Version of the Graph using the given operator.
	 * 
	 * @param g
	 *            the GraphManager of the currently active Graph
	 * 
	 */
	public void calculate(GraphManager g);

	/**
	 * returns the name of the Metric.
	 */
	public String getName();

}
