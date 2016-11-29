package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionMode;
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
	private Visualizer v;

	private String lastClickedID;
	
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
		switch(v.getSelectionMode()){
		case SHOW_ATTRIBUTES:
			v.setSelectedNodeID(id);
			v.setSelectedEdgeID(null);
			break;
		case CREATE_EDGE:
			if(lastClickedID==null){
				lastClickedID=id;
			} else {
				v.getGraph().addEdge(Integer.toString(new Random().nextInt()), lastClickedID, id);
				lastClickedID = null;
			}
			break;
		case SELECT_EDGE:
			Debug.out("id =" +lastClickedID);
			if(lastClickedID==null){
				lastClickedID=id;
			} else {
				Edge e = v.getGraph().getNode(lastClickedID).getEdgeToward(id);
				if(e != null){
				v.setSelectedEdgeID(e.getId());
				v.setSelectedNodeID(null);
				lastClickedID = null;
				//v.getGraph().getEdge(v.getSelectedEdgeID()).setAttribute("ui.style", values);
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
