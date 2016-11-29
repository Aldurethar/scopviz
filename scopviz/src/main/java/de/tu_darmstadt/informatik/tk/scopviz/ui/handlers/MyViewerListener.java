package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.util.Random;

import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.main.SelectionMode;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

public class MyViewerListener implements ViewerListener {
	private Visualizer v;
	private String lastClickedID;
	public MyViewerListener(Visualizer viz) {
		v=viz;
	}

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
			if(lastClickedID==null){
				lastClickedID=id;
			} else {
				v.setSelectedEdgeID(v.getGraph().getNode(lastClickedID).getEdgeToward(id));
				v.setSelectedNodeID(null);
				lastClickedID = null;
				//v.getGraph().getEdge(v.getSelectedEdgeID()).setAttribute("ui.style", values);
			}
			break;
		default:
			break;
			//PropertiesManager.setItemsProperties(id);
		}
	}

	@Override
	public void buttonReleased(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewClosed(String viewName) {
		// TODO Auto-generated method stub
		
	}

}
