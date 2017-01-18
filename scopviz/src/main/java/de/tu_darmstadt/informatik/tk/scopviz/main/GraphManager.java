package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.PropertiesManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyMouseManager;

/**
 * Interface between GUI and internal Graph representation. Manages internal
 * representation of the Graph to accommodate creation and deletion of nodes and
 * edges.
 * 
 * @author Jascha Bohne
 * @version 3.0.0.0
 *
 */
public class GraphManager {

	/** The Graph this instance of GraphManager manages. */
	protected MyGraph g;

	/**
	 * The Stylesheet for this Graph, excluding parts that can be set by
	 * NodeGraphics.
	 */
	protected String stylesheet = "";

	/** The last Node that was deleted. */
	protected Node deletedNode;
	/** The last Edge that was deleted. */
	protected LinkedList<Edge> deletedEdges = new LinkedList<>();

	/** The currently selected Node, mutually exclusive with selectedEdgeID. */
	protected String selectedNodeID = null;
	/** The currently selected Edge, mutually exclusive with selectedNodeID. */
	protected String selectedEdgeID = null;

	/** The ViewPanel the Graph is drawn in. */
	protected ViewPanel view;

	/** The Path on Disk the Graph will be saved to. */
	protected String currentPath;

	/** The Viewer the Graph provides, grants Access to Camera Manipulation. */
	protected Viewer viewer;
	/**
	 * The Pipe that notifies the underlying Graph of any Changes within the
	 * graphic Representation.
	 */
	protected ViewerPipe fromViewer;

	/**
	 * Creates a new Manager for the given graph.
	 * 
	 * @param graph
	 *            the graph this visualizer should handle
	 */
	public GraphManager(MyGraph graph) {
		g = graph;
		/* Viewer */ viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		view = viewer.addDefaultView(false);
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
		/* ViewerPipe */fromViewer = viewer.newViewerPipe();
		view.setMouseManager(new MyMouseManager(this));
		fromViewer.addSink(graph);
		fromViewer.removeElementSink(graph);
	}

	/**
	 * Deletes the Node corresponding to the given ID from the Graph. The
	 * referenced Graph is modified directly. Will throw an
	 * ElementNotFoundException, when the Node is not Found Will also remove all
	 * Edges connected to the given Node
	 * 
	 * @param id
	 *            the ID of the node that will be removed
	 */
	public void deleteNode(final String id) {
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		// Edges have to be deleted first because they clear deletedNode
		// and need the Node to still be in the Graph
		deleteEdgesOfNode(id);
		deletedNode = g.removeNode(id);
		// System.out.println("test-del");
	}

	/**
	 * Deletes the Edge corresponding to the given ID from the Graph. The
	 * referenced Graph is modified directly. Will throw an
	 * ElementNotFoundException, when the Edge is not Found
	 * 
	 * @param id
	 *            the ID of the Edge that will be removed
	 */
	public void deleteEdge(final String id) {
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		deletedEdges.add(g.removeEdge(id));
	}

	/**
	 * Deletes all Edges connected to the given Node. The referenced Graph is
	 * modified directly. Will throw an ElementNotFoundException if the Node is
	 * not Found
	 *
	 * @param id
	 *            the Id of the Node, whose Edges shall be removed
	 */
	public void deleteEdgesOfNode(final String id) {
		Node node = g.getNode(id);
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		Edge[] temp = new Edge[0];
		temp = g.getEdgeSet().toArray(temp);

		for (Edge e : temp) {
			if (e.getSourceNode().equals(node) || e.getTargetNode().equals(node)) {
				// adds the Edge to the list of deleted Edges and remove sit
				// from the Graph
				deletedEdges.add(g.removeEdge(e));
			}
		}
	}

	/**
	 * Undoes the last deleting operation on the given Graph. Deleting
	 * operations are: deleteNode, deleteEdge and deleteEdgesOfNode. Only undoes
	 * the last deleting operation even if that operation didn't change the
	 * Graph
	 */
	public void undelete() {
		// System.out.println("test-undel");
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		if (deletedNode != null) {
			for (String s : deletedNode.getAttributeKeySet()) {
				attributes.put(s, deletedNode.getAttribute(s));
			}
			g.addNode(deletedNode.getId());
			g.getNode(deletedNode.getId()).addAttributes(attributes);
		}

		for (Edge e : deletedEdges) {
			attributes = new HashMap<String, Object>();
			for (String s : e.getAttributeKeySet()) {
				attributes.put(s, e.getAttribute(s));
			}
			g.addEdge(e.getId(), (Node) e.getSourceNode(), (Node) e.getTargetNode());
			g.getEdge(e.getId()).addAttributes(attributes);
		}
	}

	/**
	 * returns a View of the Graph. The View lives in the Swing Thread and the
	 * Graph in the Main thread.
	 * 
	 * 
	 * @return a View of the Graph, inheriting from JPanel
	 */
	public ViewPanel getView() {
		return view;
	}

	/**
	 * Returns the ID of the currently selected Node.
	 * 
	 * @return the node's ID
	 */
	public String getSelectedNodeID() {
		return selectedNodeID;
	}

	/**
	 * Returns the ID of the currently selected Edge.
	 * 
	 * @return the edge's ID
	 */
	public String getSelectedEdgeID() {
		return selectedEdgeID;
	}

	/**
	 * Selects the Node with the given ID, resets Edge selection.
	 * 
	 * @param nodeID
	 *            the ID of the Node to select
	 */
	public void selectNode(String nodeID) {
		if (nodeID != null && g.getNode(nodeID) != null) {
			deselect();
			this.selectedNodeID = nodeID;

			Node n = g.getNode(nodeID);
			// set selected node color to red
			String nodeType = n.getAttribute("ui.class");
			n.changeAttribute("ui.style", "fill-mode: image-scaled; fill-image: url('src/main/resources/png/" + nodeType
					+ "_red.png'); size: 15px;");
			n.changeAttribute("ui.class", nodeType + "_red");
			PropertiesManager.setItemsProperties();
		}
	}

	/**
	 * Selects the Edge with the given ID, resets Node selection.
	 * 
	 * @param edgeID
	 *            the ID of the Edge to select
	 */
	public void selectEdge(String edgeID) {
		if (edgeID != null && g.getEdge(edgeID) != null) {
			deselect();
			this.selectedEdgeID = edgeID;

			// set selected edge color to red
			g.getEdge(getSelectedEdgeID()).changeAttribute("ui.style", "fill-color: #FF0000;");
			PropertiesManager.setItemsProperties();
		}
	}

	/**
	 * Deselect any currently selected nodes or edges.
	 */
	// TODO call this before save
	public void deselect() {
		// Set last selected Edge Color to Black
		if (getSelectedEdgeID() != null) {
			g.getEdge(getSelectedEdgeID()).changeAttribute("ui.style", "fill-color: #000000;");
		}
		// Set last selected Node color to black
		else if (getSelectedNodeID() != null) {
			Node n = g.getNode(getSelectedNodeID());
			String nodeType = n.getAttribute("ui.class");
			n.removeAttribute("ui.style");
			n.changeAttribute("ui.style", "fill-color: #000000; size: 10px;");
			n.changeAttribute("ui.class", nodeType.split("_")[0]);
		}
		this.selectedNodeID = null;
		this.selectedEdgeID = null;
	}

	/**
	 * Returns a reference to the Graph object managed by this visualizer.
	 * 
	 * @return the graph
	 */
	public Graph getGraph() {
		return g;
	}

	/**
	 * Zooms in the view of the graph by 5 percent.
	 */
	public void zoomIn() {
		zoom(-0.05);
	}

	/**
	 * Zooms out the view of the graph by 5 percent.
	 */
	public void zoomOut() {
		zoom(0.05);
	}

	/**
	 * Zooms the view by the given Amount, positive values zoom out, negative
	 * values zoom in.
	 * 
	 * @param amount
	 *            the amount of zoom, should usually be between -0.2 and 0.2 for
	 *            reasonable zoom.
	 */
	public void zoom(double amount) {
		view.getCamera().setViewPercent(view.getCamera().getViewPercent() * (1 + amount));
	}

	/**
	 * Pumps the Pipe from the graphical Representation to the underlying Graph,
	 * propagating all Changes made.
	 */
	public void pumpIt() {
		fromViewer.pump();
	}

	@Override
	public String toString() {
		return "Visualizer for Graph \"" + g.getId() + "\"";
	}

	/**
	 * Returns the current Save Path on Disk for the Graph.
	 * 
	 * @return the current Path
	 */
	public String getCurrentPath() {
		return currentPath;
	}

	/**
	 * Sets the Save Path.
	 * 
	 * @param currentPath
	 *            the new Path to set
	 */
	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	/**
	 * Adds a <b>Copy</b> of the given Edge to the graph. The Copy retains the
	 * ID and all attributes.
	 * 
	 * @param e
	 *            the Edge to be added to the graph
	 */
	public void addEdge(Edge e) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : e.getAttributeKeySet()) {
			attributes.put(s, e.getAttribute(s));
		}
		g.addEdge(e.getId(), (Node) e.getSourceNode(), (Node) e.getTargetNode());
		g.getEdge(e.getId()).addAttributes(attributes);
	}

	/**
	 * Adds a <b>Copy</b> of the given Node to the graph. The Copy retains the
	 * ID and all attributes.
	 * 
	 * @param n
	 *            the Node to be added to the graph
	 */
	public void addNode(Node n) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : n.getAttributeKeySet()) {
			attributes.put(s, deletedNode.getAttribute(s));
		}
		g.addNode(n.getId());
		g.getNode(n.getId()).addAttributes(attributes);
	}

	/**
	 * Returns the smallest X Coordinate of any Node in the Graph.
	 * 
	 * @return the smallest X Coordinate in the Graph
	 */
	public double getMinX() {
		double currentMin = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMin > (Double) n.getAttribute("x")) {
				currentMin = (Double) n.getAttribute("x");
			}
		}
		return currentMin;
	}

	/**
	 * Returns the biggest X Coordinate of any Node in the Graph.
	 * 
	 * @return the biggest X Coordinate in the Graph
	 */
	public double getMaxX() {
		double currentMax = Double.MIN_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMax < (Double) n.getAttribute("x")) {
				currentMax = (Double) n.getAttribute("x");
			}
		}
		return currentMax;
	}

	/**
	 * Returns the smallest Y Coordinate of any Node in the Graph.
	 * 
	 * @return the smallest Y Coordinate in the Graph
	 */
	public double getMinY() {
		double currentMin = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("y") && currentMin > (Double) n.getAttribute("y")) {
				currentMin = (Double) n.getAttribute("y");
			}
		}
		return currentMin;
	}

	/**
	 * Returns the biggest Y Coordinate of any Node in the Graph.
	 * 
	 * @return the biggest Y Coordinate in the Graph
	 */
	public double getMaxY() {
		double currentMax = Double.MIN_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("y") && currentMax < (Double) n.getAttribute("y")) {
				currentMax = (Double) n.getAttribute("y");
			}
		}
		return currentMax;
	}

	/**
	 * Converts the Coordinates of all Nodes into a saveable and uniform Format.
	 */
	public void correctCoordinates() {
		Point3 coords;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("xyz")) {
				coords = Toolkit.nodePointPosition(n);
				n.setAttribute("x", coords.x);
				n.setAttribute("y", coords.y);
				n.removeAttribute("xyz");
			}
		}
	}

	/**
	 * Converts the weight property into a label to display on the Graph.
	 * Removes all labels if that option is set
	 */
	public void handleEdgeWeight() {
		Edge e = null;
		Iterator<Edge> allEdges = g.getEdgeIterator();

		while (allEdges.hasNext()) {
			e = allEdges.next();
			if (!e.hasAttribute("weight")) {
				e.addAttribute("weight", OptionsManager.getDefaultWeight());
			}
			if (OptionsManager.isWeightShown()) {
				e.setAttribute("ui.label", e.getAttribute("weight").toString());
			} else {
				e.removeAttribute("ui.label");
			}
		}
	}

	/**
	 * Returns the Stylesheet used by the Graph.
	 * 
	 * @return the Stylesheet in use
	 */
	public String getStylesheet() {
		return stylesheet;
	}

	/**
	 * Sets the Stylesheet to be used by the Graph.
	 * 
	 * @param stylesheet
	 *            the new stylesheet to use
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
		g.removeAttribute("ui.stylesheet");
		String completeStylesheet = stylesheet;
		completeStylesheet = completeStylesheet.concat(OptionsManager.getNodeGraphics());
		completeStylesheet = completeStylesheet.concat(OptionsManager.getLayerStyle((Layer) g.getAttribute("layer")));
		g.addAttribute("ui.stylesheet", completeStylesheet);
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when an Edge is added.
	 * 
	 * @param e
	 *            the EdgeCreatedListener
	 */
	public void addEdgeCreatedListener(EdgeCreatedListener e) {
		((MyGraph) g).addEdgeCreatedListener(e);
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when a Node is added.
	 * 
	 * @param n
	 *            the NodeCreatedListener
	 */
	public void addNodeCreatedListener(NodeCreatedListener n) {
		((MyGraph) g).addNodeCreatedListener(n);
	}

	/**
	 * Updates the Stylesheet, causing any changes to it to come into effect.
	 */
	public void updateStylesheet() {
		setStylesheet(this.stylesheet);
	}

}
