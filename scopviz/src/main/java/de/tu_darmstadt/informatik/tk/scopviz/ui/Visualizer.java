package de.tu_darmstadt.informatik.tk.scopviz.ui;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * Interface between GUI and internal Graph representation.
 * 
 * @version 1.0.0.0
 * @author jascha-b
 *
 */
public final class Visualizer {

	/**
	 * Private Constructor to prevent instantiation.
	 */
	private Visualizer() {
	}

	// TODO add getview with size
	/**
	 * returns a View of the Graph. The View is in the Swing Thread and the
	 * Graph in the Main thread.
	 * 
	 * @param g
	 *            the Graph that the view is based on
	 * @return a View of the Graph, inheriting from JPanel
	 */
	public static ViewPanel getView(final Graph g) {
		Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		ViewPanel view = viewer.addDefaultView(false);
		return view;
	}
}
