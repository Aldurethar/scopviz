package de.tu_darmstadt.informatik.tk.scopviz.ui.handlers;

import java.awt.event.MouseEvent;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.util.DefaultMouseManager;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.EdgeSelectionHelper;
import de.tu_darmstadt.informatik.tk.scopviz.main.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;

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
	 * The Id of the Node that was last clicked.
	 */
	private String lastClickedID;

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
	protected void mouseButtonPress(MouseEvent event) {
		view.requestFocus();

		Graph graph = graphManager.getGraph();
		Point3 cursorPos = graphManager.getView().getCamera().transformPxToGu(event.getX(), event.getY());
		Node n;
		Edge selectedEdge = EdgeSelectionHelper.getClosestEdge(cursorPos);

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
			n = graph.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "standard");
			graphManager.selectNode(n.getId());
			Debug.out("Added Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

			break;

		case CREATE_SOURCE_NODE:
			n = graph.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "source");
			graphManager.selectNode(n.getId());
			Debug.out("Added Source Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/" + cursorPos.y
					+ ")");

			break;

		case CREATE_SINK_NODE:
			n = graph.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "sink");
			graphManager.selectNode(n.getId());
			Debug.out(
					"Added Sink Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/" + cursorPos.y + ")");

			break;

		case CREATE_PROC_NODE:
			n = graph.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "procEn");
			graphManager.selectNode(n.getId());
			Debug.out("Added ProcEn Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/" + cursorPos.y
					+ ")");

			break;

		case CREATE_OPERATOR_NODE:
			n = graph.addNode(Main.getInstance().getUnusedID());
			n.setAttribute("xyz", cursorPos);
			n.setAttribute("ui.class", "operator");
			graphManager.selectNode(n.getId());
			Debug.out("Added Operator Node with ID " + n.getId() + " at Position (" + cursorPos.x + "/" + cursorPos.y
					+ ")");

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
		// NOP
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
	protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
		view.freezeElement(element, true);
		String id = element.getId();

		// if in Creation Mode, try to create an Edge with the Node that was
		// clicked on
		if (Main.getInstance().getCreationMode() != CreationMode.CREATE_NONE) {
			createEdges(id);
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
		} else if (event.getButton() == MouseEvent.BUTTON2) {
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
	public void mouseDragged(MouseEvent event) {
		// If the Mouse is dragging an Element, move it
		if (curElement != null) {
			elementMoving(curElement, event);

			// If the Middle Mouse Button is pressed, move the Camera following
			// the dragging gesture
		} else if (event.getButton() == MouseEvent.BUTTON2) {

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
	public void mouseReleased(MouseEvent event) {
		if (curElement != null) {
			mouseButtonReleaseOffElement(curElement, event);
			curElement = null;
		} else {
		}
	}

	/**
	 * Create Edges based on CreateMode
	 * 
	 * @param id
	 */
	private void createEdges(String id) {
		String newID = null;
		switch (Main.getInstance().getCreationMode()) {

		case CREATE_DIRECTED_EDGE:

			if (lastClickedID == null) {
				lastClickedID = id;
				selectNodeForEdgeCreation(lastClickedID);
			} else {
				if (!id.equals(lastClickedID)) {
					newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id, true);
					Debug.out("Created an directed edge with Id " + newID + " between " + lastClickedID + " and " + id);

					deselectNodesAfterEdgeCreation(lastClickedID);

					lastClickedID = null;
					graphManager.selectEdge(newID);
				}
			}
			break;

		case CREATE_UNDIRECTED_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
				selectNodeForEdgeCreation(lastClickedID);
			} else {
				if (!id.equals(lastClickedID)) {
					newID = Main.getInstance().getUnusedID();
					graphManager.getGraph().addEdge(newID, lastClickedID, id);

					Debug.out(
							"Created an undirected edge with Id " + newID + " between " + lastClickedID + " and " + id);

					deselectNodesAfterEdgeCreation(lastClickedID);
					lastClickedID = null;
					graphManager.selectEdge(newID);
				}
			}
			break;

		default:
			break;
		}
		PropertiesManager.setItemsProperties();

		// controller.createModusText.setText(Main.getInstance().getCreationMode().toString());

	}

	/**
	 * Selects a Node as the starting point for creating a new Edge.
	 * 
	 * @param nodeID
	 *            the ID of the Node to select
	 */
	private void selectNodeForEdgeCreation(String nodeID) {
		Node n = graphManager.getGraph().getNode(nodeID);
		String nodeType = n.getAttribute("ui.class");
		nodeType = nodeType.split("_")[0];
		n.changeAttribute("ui.style", "fill-mode: image-scaled; fill-image: url('src/main/resources/png/" + nodeType
				+ "_green.png'); size: 15px;");
		n.changeAttribute("ui.class", nodeType + "_green");
	}

	/**
	 * Reset the Selection of the Node after Edge has been successfully created.
	 * 
	 * @param nodeID
	 *            the Id of the node to deselect.
	 */
	private void deselectNodesAfterEdgeCreation(String nodeID) {
		Node n = graphManager.getGraph().getNode(nodeID);
		String nodeType = n.getAttribute("ui.class");
		n.removeAttribute("ui.style");
		n.changeAttribute("ui.style", "fill-color: #000000; size: 10px;");
		n.changeAttribute("ui.class", nodeType.split("_")[0]);
	}
}
