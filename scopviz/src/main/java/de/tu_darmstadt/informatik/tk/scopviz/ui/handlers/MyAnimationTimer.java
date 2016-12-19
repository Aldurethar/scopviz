package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GUIController;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import javafx.animation.AnimationTimer;

public class MyAnimationTimer extends AnimationTimer {

	private static GUIController guiController;
	long time = -1;

	@Override
	public void handle(long now) {
		if (Main.getInstance().getGraphManager() != null) {
			Main.getInstance().getGraphManager().pumpIt();
			Main.getInstance().getGraphManager().correctCoordinates();
			Main.getInstance().getGraphManager().handleEdgeWeight();
			try {
				guiController.createModusText.setText(Main.getInstance().getCreationMode().toString());
				guiController.selectModusText.setText(Main.getInstance().getSelectionMode().toString());
				guiController.actualLayerText.setText(GraphDisplayManager.getCurrentLayer().toString());
			} catch (NullPointerException e) {
				// TODO find a better soultion for the null pointer that pops up
				// on startup
			}
		}
	}

	public static void setGUIController(GUIController con) {
		guiController = con;
	}
}
