package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.LinkedList;

public interface ScopvizGraphOperator {

	/**
	 * calculates a new Version of the Graph using the given operator
	 * 
	 * @param g
	 *            a MyGraph
	 * @return a list of Graphs that is the result of the operator on the Graph
	 *         g
	 */
	public LinkedList<MyGraph> calculate(MyGraph g);
}
