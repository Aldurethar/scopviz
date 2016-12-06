package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import org.graphstream.graph.Edge;
import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreateModus;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;
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
	private Visualizer visualizer;

	private String lastClickedID;

	/**
	 * Creates a new MyViewerListener object.
	 * 
	 * @param viz
	 *            the visualizer that manages the view this listener listens to
	 */
	public MyViewerListener(Visualizer viz) {
		visualizer = viz;
	}

	/**
	 * Gets called whenever one of the nodes within the viewer is clicked.
	 * 
	 * @param id
	 *            the id of the Node that was clicked
	 */
	@Override
	public void buttonPushed(String id) {
		if(Main.getInstance().getCreateModus() != CreateModus.CREATE_NONE){
			return;
		}
		switch (Main.getInstance().getSelectModus()) {
		case SELECT_NODES:
			visualizer.setSelectedNodeID(id);
			visualizer.setSelectedEdgeID(null);
			break;
		case SELECT_EDGES:
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				Edge e = visualizer.getGraph().getNode(lastClickedID).getEdgeToward(id);
				if (e != null) {
					visualizer.setSelectedEdgeID(e.getId());
					visualizer.setSelectedNodeID(null);
					lastClickedID = null;
				} else {
					lastClickedID = id;
				}
			}
			break;
/*		case CREATE_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				if (!id.equals(lastClickedID)) {
					String newID = Main.getInstance().getUnusedID();
					visualizer.getGraph().addEdge(newID, lastClickedID, id);

					Debug.out("Created an edge with Id " + newID + " between " + lastClickedID + " and " + id);

					lastClickedID = null;
					visualizer.setSelectedNodeID(null);
					visualizer.setSelectedEdgeID(newID);
				}
			}
			break;*/
		default:
			break;
		}
		PropertiesManager.setItemsProperties();
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
