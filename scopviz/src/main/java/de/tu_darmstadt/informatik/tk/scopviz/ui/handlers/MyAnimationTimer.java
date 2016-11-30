package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.animation.AnimationTimer;

public class MyAnimationTimer extends AnimationTimer{

	@Override
	public void handle(long now) {
		Main.getInstance().getVisualizer().pumpIt();
		
	}

}
