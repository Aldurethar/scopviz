package de.tu_darmstadt.informatik.tk.scopviz.graphs;

/**
 * Interface for listeners on new Edges being created.
 * 
 * @author Jascha Bohne
 * @version 1.0
 *
 */
public interface EdgeCreatedListener {

	/**
	 * Should be called whenever a new Edge has been created.
	 * 
	 * @param e
	 *            the newly created Edge
	 * @param graphID
	 *            the ID of the Graph that had the Edge added to it
	 */
	public void edgeCreated(MyEdge e, String graphID);
}
