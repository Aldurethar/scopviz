package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import org.graphstream.graph.Edge;
import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;

/**
 * Listener to react to changes in the graph viewer.
 * 
 * @author Jascha Bohne
 * @version 1.0
 *
 */
public class MyViewerListener implements ViewerListener {

	/**
	 * Create more then one Edge at a time mode
	 */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/**
	 * Reference to the visualizer for easier access.
	 */
	private GraphManager graphManager;

	private String lastClickedID;

	/**
	 * Creates a new MyViewerListener object.
	 * 
	 * @param viz
	 *            the visualizer that manages the view this listener listens to
	 */
	public MyViewerListener(GraphManager viz) {
		graphManager = viz;
	}

	/**
	 * Gets called whenever one of the nodes within the viewer is clicked.
	 * 
	 * @param id
	 *            the id of the Node that was clicked
	 */
	@Override
	public void buttonPushed(String id) {
		if (Main.getInstance().getCreationMode() != CreationMode.CREATE_NONE) {
			createEdges(id);
			return;
		}
		switch (Main.getInstance().getSelectModus()) {
		case SELECT_NODES:
			graphManager.setSelectedNodeID(id);
			graphManager.setSelectedEdgeID(null);
			break;
		case SELECT_EDGES:
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				Edge e = graphManager.getGraph().getNode(lastClickedID).getEdgeToward(id);
				if (e != null) {
					graphManager.setSelectedEdgeID(e.getId());
					graphManager.setSelectedNodeID(null);
					lastClickedID = null;
				} else {
					lastClickedID = id;
				}
			}
			break;
		default:
			break;
		}
		PropertiesManager.setItemsProperties();
	}

	/**
	 * Create Edges based on CreateMode
	 * 
	 * @param id
	 */
	private void createEdges(String id) {

		switch (Main.getInstance().getCreationMode()) {

		case CREATE_DIRECTED_EDGE:

			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				if (!id.equals(lastClickedID)) {
					String newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id, true);
					Debug.out("Created an directed edge with Id " + newID + " between " + lastClickedID + " and " + id);

					lastClickedID = null;
					graphManager.setSelectedNodeID(null);
					graphManager.setSelectedEdgeID(newID);
				}
			}
			break;

		case CREATE_UNDIRECTED_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				if (!id.equals(lastClickedID)) {
					String newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id);

					Debug.out(
							"Created an undirected edge with Id " + newID + " between " + lastClickedID + " and " + id);

					lastClickedID = null;
					graphManager.setSelectedNodeID(null);
					graphManager.setSelectedEdgeID(newID);
				}
			}
			break;

		default:
			break;
		}

		PropertiesManager.setItemsProperties();

		if (!CREATE_MORE_THEN_ONE) {
			Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
		}
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
