package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

/**
 * Listener to react to changes in the graph viewer.
 * 
 * @author Jascha Bohne
 * @version 1.0
 *
 */
public class MyViewerListener implements ViewerListener {

	/**
	 * Reference to the visualizer for easier access.
	 */
	private Visualizer v;

	/**
	 * Creates a new MyViewerListener object.
	 * 
	 * @param viz
	 *            the visualizer that manages the view this listener listens to
	 */
	public MyViewerListener(Visualizer viz) {
		v = viz;
	}

	/**
	 * Gets called whenever one of the nodes within the viewer is clicked.
	 * 
	 * @param id
	 *            the id of the Node that was clicked
	 */
	@Override
	public void buttonPushed(String id) {
		v.setSelectedNodeID(id);
	}

	/**
	 * Gets called whenever the click on the node is released.
	 */
	@Override
	public void buttonReleased(String id) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets called whenever the view is closed.
	 */
	@Override
	public void viewClosed(String viewName) {
		// TODO Auto-generated method stub

	}

}
