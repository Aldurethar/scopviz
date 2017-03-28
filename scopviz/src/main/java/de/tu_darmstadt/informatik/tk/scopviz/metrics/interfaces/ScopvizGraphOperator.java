package de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;

public interface ScopvizGraphOperator {
	
	/**
	 * calculates a new Version of the Graph using the given operator.
	 * 
	 * @param g
	 *            a MyGraph
	 * @return a list of Graphs that is the result of the operator on the Graph
	 *         g
	 */
	public void calculate(GraphManager g);

	/**
	 * returns the name of the Metric.
	 */
	public String getName();

}
