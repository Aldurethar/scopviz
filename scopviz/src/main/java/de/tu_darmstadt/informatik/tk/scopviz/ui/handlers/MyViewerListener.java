package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
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
	 * Create more then one Edge at a time mode.
	 */
	public static final Boolean CREATE_MORE_THEN_ONE = true;

	/**
	 * Reference to the visualizer for easier access.
	 */
	private GraphManager graphManager;

	/**
	 * The Id of the Node that was last clicked.
	 */
	private String lastClickedID;

	/**
	 * GUIController reference.
	 */
	private static GUIController controller;

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
	 * Set GUIController
	 * 
	 * @param guiController
	 */
	public static void setGUIController(GUIController guiController) {
		controller = guiController;
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
		deselectNodesAfterEdgeCreation(id);

		switch (Main.getInstance().getSelectionMode()) {
		case SELECT_NODES:
			graphManager.selectNode(id);
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
		String newID = null;
		switch (Main.getInstance().getCreationMode()) {

		case CREATE_DIRECTED_EDGE:

			if (lastClickedID == null) {
				lastClickedID = id;
				selectNodeForEdgeCreation(lastClickedID);
			} else {
				if (!id.equals(lastClickedID)) {
					newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id, true);
					Debug.out("Created an directed edge with Id " + newID + " between " + lastClickedID + " and " + id);

					deselectNodesAfterEdgeCreation(lastClickedID);

					lastClickedID = null;
					graphManager.selectEdge(newID);
				}
			}
			break;

		case CREATE_UNDIRECTED_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
				selectNodeForEdgeCreation(lastClickedID);
			} else {
				if (!id.equals(lastClickedID)) {
					newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id);

					Debug.out(
							"Created an undirected edge with Id " + newID + " between " + lastClickedID + " and " + id);

					deselectNodesAfterEdgeCreation(lastClickedID);
					lastClickedID = null;
					graphManager.selectEdge(newID);
				}
			}
			break;

		default:
			break;
		}
		PropertiesManager.setItemsProperties();

		controller.createModusText.setText(Main.getInstance().getCreationMode().toString());

		if (!CREATE_MORE_THEN_ONE) {
			Main.getInstance().setCreationMode(CreationMode.CREATE_NONE);
		}
	}

	/**
	 * Selects a Node as the starting point for creating a new Edge.
	 * 
	 * @param nodeID
	 *            the ID of the Node to select
	 */
	private void selectNodeForEdgeCreation(String nodeID) {
		graphManager.getGraph().getNode(nodeID).changeAttribute("ui.style", "fill-color: #00FF00; size: 15px;");
	}

	/**
	 * Reset the Selection of the Node after Edge has been successfully created.
	 * 
	 * @param nodeID
	 *            the Id of the node to deselect.
	 */
	private void deselectNodesAfterEdgeCreation(String nodeID) {
		String uiStyle = "fill-color: #000000; size: 10px;";
		graphManager.getGraph().getNode(nodeID).changeAttribute("ui.style", uiStyle);
	}

	/**
	 * Gets called whenever the click on the node is released.
	 */
	@Override
	public void buttonReleased(String id) {

	}

	/**
	 * Gets called whenever the view is closed.
	 */
	@Override
	public void viewClosed(String viewName) {

	}

}
