package de.tu_darmstadt.informatik.tk.scopviz.main;

import org.graphstream.graph.Node;

/**
 * Interface for Listeners on new Nodes being created.
 * 
 * @author Jascha Bohne
 * @version 1.0
 */
public interface NodeCreatedListener {

	/**
	 * Should be called whenever a new Node was created.
	 * 
	 * @param n
	 *            the newly created Node
	 * @param graphID
	 *            the Id of the Graph the Node was added to.
	 */
	public void nodeCreated(Node n, String graphID);
}
