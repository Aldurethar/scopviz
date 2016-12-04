package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.animation.AnimationTimer;

public class MyAnimationTimer extends AnimationTimer{

	long time = -1;
	@Override
	public void handle(long now) {
		Main.getInstance().getVisualizer().pumpIt();
		if (time == -1)
			time = now;
		else {
			if (time + 2000000000 < now) {
				Main.getInstance().load2ndGraph();
				Debug.out("blub");
				time += 2000000000;
			}
		}
	}

}
