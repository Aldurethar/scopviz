package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.animation.AnimationTimer;

public class MyAnimationTimer extends AnimationTimer {

	long time = -1;

	@Override
	public void handle(long now) {
		if(Main.getInstance().getVisualizer() != null){
			Main.getInstance().getVisualizer().pumpIt();
		}
		// TODO: For Demo purposes only

	}

}
