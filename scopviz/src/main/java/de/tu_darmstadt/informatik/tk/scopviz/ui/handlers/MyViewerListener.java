package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import org.graphstream.graph.Edge;
import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreateModus;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
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
	 * Create more then one Edge at a time mode
	 */
	public static final Boolean CREATE_MORE_THEN_ONE = true; 

	/**
	 * Reference to the visualizer for easier access.
	 */
	private Visualizer visualizer;

	private String lastClickedID;
	
	/**
	 * GUIController reference
	 */
	private static GUIController controller;

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
	 * Set GUIController 
	 * @param guiController
	 */
	public static void setGUIController(GUIController guiController){
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
		if(Main.getInstance().getCreateModus() != CreateModus.CREATE_NONE){
			createEdges(id);
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
		default:
			break;
		}
		PropertiesManager.setItemsProperties();
	}
	
	
	/**
	 * Create Edges based on CreateMode
	 * @param id
	 */
	private void createEdges(String id){
		
		switch(Main.getInstance().getCreateModus()){
		
		case CREATE_DIRECTED_EDGE:
			
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				if (!id.equals(lastClickedID)) {
					String newID = Main.getInstance().getUnusedID();
					visualizer.getGraph().addEdge(newID, lastClickedID, id, true);
					Debug.out("Created an directed edge with Id " + newID + " between " + lastClickedID + " and " + id);

					lastClickedID = null;
					visualizer.setSelectedNodeID(null);
					visualizer.setSelectedEdgeID(newID);
				}
			}
			break;
			
		case CREATE_UNDIRECTED_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
			} else {
				if (!id.equals(lastClickedID)) {
					String newID = Main.getInstance().getUnusedID();
					visualizer.getGraph().addEdge(newID, lastClickedID, id);

					Debug.out("Created an undirected edge with Id " + newID + " between " + lastClickedID + " and " + id);

					lastClickedID = null;
					visualizer.setSelectedNodeID(null);
					visualizer.setSelectedEdgeID(newID);
				}
			}
			break;
			
		default:
			break;
		}
		
		PropertiesManager.setItemsProperties();
		
		controller.createModusText.setText(Main.getInstance().getCreateModus().toString());
		
		if(!CREATE_MORE_THEN_ONE){
			Main.getInstance().setCreateModus(CreateModus.CREATE_NONE);
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
