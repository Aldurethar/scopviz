package de.tu_darmstadt.informatik.tk.scopviz.main;

import org.graphstream.ui.view.ViewerListener;

import de.tu_darmstadt.informatik.tk.scopviz.ui.Visualizer;

public class MyViewerListener implements ViewerListener {
	private Visualizer v;
	public MyViewerListener(Visualizer viz) {
		v=viz;
	}

	@Override
	public void buttonPushed(String id) {
		v.setSelectedNodeID(id);
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
