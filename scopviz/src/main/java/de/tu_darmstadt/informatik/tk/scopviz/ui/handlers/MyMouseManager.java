package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.awt.event.MouseEvent;

import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;

public class MyMouseManager /*implements MouseManager*/ extends DefaultMouseManager{
	
	/**
	 * The view this manager operates upon.
	 */
	//protected View view;

	/**
	 * The graph to modify according to the view actions.
	 */
	//protected GraphicGraph graph;
/*
	public void init(GraphicGraph graph, View view) {
		this.view = view;
		this.graph = graph;
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}

	public void release() {
		view.removeMouseListener(this);
		view.removeMouseMotionListener(this);
	}
*/
	protected void mouseButtonPress(MouseEvent event) {
		view.requestFocus();

		// TODO: Edge select
	}

	protected void mouseButtonRelease(MouseEvent event){
		// NOP
	}

	protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
		view.freezeElement(element, true);
		// TODO: Node Selection
		Node n = (Node) element;
		Main.getInstance().getGraphManager().selectNode(n.getId());
		
	}
/*
	protected void elementMoving(GraphicElement element, MouseEvent event) {
		view.moveElementAtPx(element, event.getX(), event.getY());
	}
*/
	protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {
		view.freezeElement(element, false);		
	}

	// Mouse Listener
/*
	protected GraphicElement curElement;

	public void mouseClicked(MouseEvent event) {
		// NOP
	}
*/
	public void mousePressed(MouseEvent event) {
		curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

		if (curElement != null) {
			mouseButtonPressOnElement(curElement, event);
		} else{
			mouseButtonPress(event);
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (curElement != null) {
			elementMoving(curElement, event);
		}
	}

	public void mouseReleased(MouseEvent event) {
		if (curElement != null) {
			mouseButtonReleaseOffElement(curElement, event);
			curElement = null;
		} else {
		}
	}
/*
	public void mouseEntered(MouseEvent event) {
		// NOP
	}

	public void mouseExited(MouseEvent event) {
		// NOP
	}

	public void mouseMoved(MouseEvent event) {
		// NOP
	}
*/	
	private static Point3 oldMousePos;
	
	private static Point3 oldViewCenter;
	
	/** Handler for remembering the Mouse Position on Mouse Button Press */
	public static final EventHandler<javafx.scene.input.MouseEvent> rememberLastClickedPosHandler = new EventHandler<javafx.scene.input.MouseEvent>() {

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			if (event.getButton() == MouseButton.MIDDLE){
				Camera cam = Main.getInstance().getGraphManager().getView().getCamera();
				oldMousePos = cam.transformPxToGu(event.getSceneX(), event.getSceneY());
				Debug.out("Zoom level = "+cam.getViewPercent());
				oldViewCenter = cam.getViewCenter();
				Debug.out("   !!!!!!  Last mouse click position remembered: " + oldMousePos.x + "/" + oldMousePos.y);
			}
		}
	};
	
	
	/**
	 * Handler for panning the Camera on Mouse Movement with pressed Middle Mouse
	 * Button
	 */
	public static final EventHandler<javafx.scene.input.MouseEvent> mouseDraggedHandler = new EventHandler<javafx.scene.input.MouseEvent>() {

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			if (event.isMiddleButtonDown()){
				Camera cam = Main.getInstance().getGraphManager().getView().getCamera();
				Point3 newMousePos = cam.transformPxToGu(event.getSceneX(), event.getSceneY());
				double offsetX = oldMousePos.x - newMousePos.x;
				double offsetY = oldMousePos.y - newMousePos.y;
				double newX = oldViewCenter.x + offsetX;
				double newY = oldViewCenter.y + offsetY;
				GraphDisplayManager.setPreferredViewCenter(new Point3(newX, newY, oldViewCenter.z));	
				Debug.out("Old center: "+oldViewCenter.x+"/"+oldViewCenter.y);
			}
		}
	};
}
