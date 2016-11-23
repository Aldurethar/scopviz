package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.awt.Dimension;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;

public class ResizeListener implements ChangeListener<Number>{

	private SwingNode swingNode;
	private Pane pane;
	
	public ResizeListener(SwingNode swingNode, Pane pane){
		this.swingNode = swingNode;
		this.pane = pane;
	}
	
	@Override
		public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
			Main.getInstance().getVisualizer().getView().setPreferredSize(new Dimension((int) pane.getWidth() - 5, (int) pane.getHeight() - 5));
			swingNode.setContent(Main.getInstance().getVisualizer().getView());
		}

}
