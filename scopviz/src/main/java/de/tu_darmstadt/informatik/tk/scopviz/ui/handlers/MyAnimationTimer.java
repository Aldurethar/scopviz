package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphHelper;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.animation.AnimationTimer;

/**
 * Handler that is called once every Frame.
 * 
 * @author Jan Enders
 * @version 1.1
 *
 */
public class MyAnimationTimer extends AnimationTimer {

	/**
	 * Starting time to prevent Exceptions.
	 */
	long time = -1;

	@Override
	public void handle(long now) {
		if (Main.getInstance().getGraphManager() != null) {
			Main.getInstance().getGraphManager().pumpIt();
			GraphHelper.correctCoordinates(Main.getInstance().getGraphManager().getGraph());
			GraphHelper.handleEdgeWeight(Main.getInstance().getGraphManager().getGraph());			
		}
	}

}
