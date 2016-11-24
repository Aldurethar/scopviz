package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.awt.Dimension;

import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Pane;

/**
 * The Listener to resize the graph view manually whenever its parent pane is
 * resized.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.0
 *
 */
public class ResizeListener implements ChangeListener<Number> {

	/**
	 * The SwingNode containing the graph view.
	 */
	private SwingNode swingNode;
	/**
	 * The parent Pane of the swingnode.
	 */
	private Pane pane;

	/**
	 * Creates a new ResizeListener for a swingNode and its parent Pane.
	 * 
	 * @param swingNode
	 *            the SwingNode
	 * @param pane
	 *            the Pane
	 */
	public ResizeListener(SwingNode swingNod, Pane pan) {
		this.swingNode = swingNod;
		this.pane = pan;
	}

	@Override
	public void changed(ObservableValue< ? extends Number> arg0, Number arg1, Number arg2) {
		Main.getInstance().getVisualizer().getView()
				.setPreferredSize(new Dimension((int) pane.getWidth() - 5, 
												(int) pane.getHeight() - 5));
		swingNode.setContent(Main.getInstance().getVisualizer().getView());
	}

}
