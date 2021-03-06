package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.awt.event.MouseEvent;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.util.DefaultMouseManager;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyEdge;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyNode;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.EdgeSelectionHelper;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.ToolboxManager;

/**
 * Mouse Manager to handle all Mouse based Interaction on the Graph Display
 * 
 * @author Jan Enders
 * @version 1.0
 */
public class MyMouseManager extends DefaultMouseManager {

	/**
	 * A Reference to the GraphManager this MouseManager is attached to for
	 * easier Access.
	 */
	private GraphManager graphManager;

	/**
	 * The Timestamp of when the Camera's View Center was last changed.
	 */
	private long lastCamUpdate = 0;

	/**
	 * The recorded Position of the Mouse Cursor (in Graph Units) when the Mouse
	 * was last clicked.
	 */
	private Point3 oldMousePos;

	/**
	 * The recorded View Center of the Camera when the Mouse was last clicked.
	 */
	private Point3 oldViewCenter;

	/**
	 * Constructor, sets Reference to the GraphManager this MouseManager is
	 * attached to.
	 * 
	 * @param manager
	 *            the GraphManager for Reference
	 */
	public MyMouseManager(GraphManager manager) {
		this.graphManager = manager;
	}

	/**
	 * Gets called whenever a Mouse Button is pressed while the Mouse is not
	 * over an Element.
	 * 
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	protected void mouseButtonPress(MouseEvent event) {
		view.requestFocus();

		Point3 cursorPos = graphManager.getView().getCamera().transformPxToGu(event.getX(), event.getY());
		MyNode n;
		MyGraph nodeProducer = new MyGraph("temp");
		MyEdge selectedEdge = EdgeSelectionHelper.getClosestEdge(cursorPos);

		switch (Main.getInstance().getCreationMode()) {

		// If not trying to create any Nodes or Edges, select the Edge that
		// was clicked on
		case CREATE_NONE:
			if (selectedEdge != null) {
				graphManager.selectEdge(selectedEdge.getId());
			}
			break;

		// Otherwise, create node based on creation Mode
		case CREATE_STANDARD_NODE:
			n = nodeProducer.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "standard");
			n.setAttribute("typeofNode", "standard");
			graphManager.addNode(n);
			graphManager.selectNode(n.getId());
			Debug.out("INFORMATION: Added Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/"
					+ cursorPos.y + ")", 1);

			break;

		case CREATE_SOURCE_NODE:
			n = nodeProducer.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "source");
			n.setAttribute("typeofNode", "source");
			graphManager.addNode(n);
			graphManager.selectNode(n.getId());
			Debug.out("INFORMATION: Added Source Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/"
					+ cursorPos.y + ")", 1);

			break;

		case CREATE_SINK_NODE:
			n = nodeProducer.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "sink");
			n.setAttribute("typeofNode", "sink");
			graphManager.addNode(n);
			graphManager.selectNode(n.getId());
			Debug.out("INFORMATION: Added Sink Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/"
					+ cursorPos.y + ")", 1);

			break;

		case CREATE_PROC_NODE:
			n = nodeProducer.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "procEn");
			n.setAttribute("typeofNode", "procEn");
			ToolboxManager.createProcMaxDialog(n);
			graphManager.addNode(n);
			graphManager.selectNode(n.getId());
			Debug.out("INFORMATION: Added ProcEn Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/"
					+ cursorPos.y + ")", 1);

			break;

		case CREATE_OPERATOR_NODE:
			n = nodeProducer.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "operator");
			n.setAttribute("typeofNode", "operator");
			ToolboxManager.createProcNeedDialog(n);
			graphManager.addNode(n);
			graphManager.selectNode(n.getId());
			Debug.out("INFORMATION: Added Operator Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/"
					+ cursorPos.y + ")", 1);

			break;

		default:
			break;
		}
	}

	/**
	 * Gets Called whenever a Mouse Button is released while the Mouse is not
	 * over an Element.
	 * 
	 * @param event
	 *            the corresponding MouseEvent
	 */
	protected void mouseButtonRelease(MouseEvent event) {
		PropertiesManager.setItemsProperties();
	}

	/**
	 * Gets called whenever a Mouse Button is pressed down while the Mouse is
	 * over an Element.
	 * 
	 * @param element
	 *            the element that was clicked on
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
		view.freezeElement(element, true);
		String id = element.getId();

		// if in Creation Mode, try to create an Edge with the Node that was
		// clicked on
		if (Main.getInstance().getCreationMode() != CreationMode.CREATE_NONE) {
			graphManager.createEdges(id);
			return;
		}

		// otherwise select the Node that was clicked
		graphManager.selectNode(id);

	}

	/**
	 * Gets called when a Mouse Button is released after having pressed it down
	 * on an Element.
	 * 
	 * @param element
	 *            the Element that the Mouse Button was pressed on
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {
		view.freezeElement(element, false);
		// update the Attributes of a Node after moving it
		PropertiesManager.setItemsProperties();
	}

	// Mouse Listener

	/**
	 * Gets called whenever a Mouse Button is pressed down.
	 * 
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		// Left Click -> Find out whether the User clicked on an Element
		if (event.getButton() == MouseEvent.BUTTON1) {
			curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

			if (curElement != null) {
				mouseButtonPressOnElement(curElement, event);
			} else {
				mouseButtonPress(event);
			}

			// Middle Click -> The User wants to Pan the Camera, record the
			// current Position and View Center
		} else if (event.getButton() == MouseEvent.BUTTON3) {
			Camera cam = Main.getInstance().getGraphManager().getView().getCamera();
			oldMousePos = cam.transformPxToGu(event.getX(), event.getY());
			oldViewCenter = cam.getViewCenter();
		}
	}

	/**
	 * Gets Called whenever the Mouse is dragged while a Mouse Button is being
	 * held down.
	 * 
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		// If the Mouse is dragging an Element, move it
		if (curElement != null) {
			elementMoving(curElement, event);

			// If the Middle Mouse Button is pressed, move the Camera following
			// the dragging gesture
		} else if (event.getButton() == MouseEvent.BUTTON3) {

			Camera cam = Main.getInstance().getGraphManager().getView().getCamera();
			Point3 newMousePos = cam.transformPxToGu(event.getX(), event.getY());
			double offsetX = oldMousePos.x - newMousePos.x;
			double offsetY = oldMousePos.y - newMousePos.y;
			double newX = oldViewCenter.x + offsetX;
			double newY = oldViewCenter.y + offsetY;

			// Only change Camera Position every 50 milliseconds, otherwise the
			// camera does weird things
			long currTime = System.currentTimeMillis();
			if (currTime - lastCamUpdate >= 50) {
				cam.setViewCenter(newX, newY, 0);
				lastCamUpdate = currTime;
			}

		}
	}

	/**
	 * Gets called when a Mouse Button is released.
	 * 
	 * @param event
	 *            the corresponding MouseEvent
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		if (curElement != null) {
			mouseButtonReleaseOffElement(curElement, event);
			curElement = null;
		} else {
			mouseButtonRelease(event);
		}
	}

}
