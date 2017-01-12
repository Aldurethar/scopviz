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

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.handlers.MyViewerListener;

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
	// The graph of this Visualizer
	private Graph g;

	// The stylesheet of this Graph - this excludes parts that can be set by
	// NodeGraphics
	private String stylesheet;

	// last deleted elements for undelete
	private Node deletedNode;
	private LinkedList<Edge> deletedEdges = new LinkedList<>();

	// Currently selected Edge or Node at least on of these is always null
	private String selectedNodeID = null;
	private String selectedEdgeID = null;

	// View Panel of the Graph
	private ViewPanel view;

	// The location the graph will be saved to
	private String currentPath;

	private Viewer viewer;
	private ViewerPipe fromViewer;

	/**
	 * Creates a new visualizer for the given graph.
	 * 
	 * @param graph
	 *            the graph this visualizer should handle
	 */
	public GraphManager(Graph graph) {
		g = graph;
		/* Viewer */ viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		view = viewer.addDefaultView(false);
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
		/* ViewerPipe */fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(new MyViewerListener(this));
		fromViewer.addSink(graph);
		fromViewer.removeElementSink(graph);
	}

	/**
	 * Deletes the Node corresponding to the given ID from the Graph. The
	 * referenced Graph is modified directly. Will throw an
	 * ElementNotFoundException, when the Node is not Found Will also remove all
	 * Edges connected to the given Node
	 * 
	 * @param g
	 *            the Graph with the Node that shall be removed
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
	 * @param g
	 *            the Graph with the Edge that shall be removed
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
	 * @param g
	 *            the Graph containing the Node
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
	 * 
	 * @param g
	 *            the Graph, whose Elements shall be undeleted
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
	 * returns a View of the Graph. The View is in the Swing Thread and the
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
	 * Sets the ID for the currently selected node, effectively selecting the
	 * node with that ID.
	 * 
	 * @param selectedNodeID
	 *            the ID of the node to select
	 */
	private void setSelectedNodeID(String selectedNodeID) {
		this.selectedNodeID = selectedNodeID;
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
	 * Sets the ID for the currently selected edge, effectively selecting the
	 * edge with that ID.
	 * 
	 * @param selectedEdgeID
	 *            the ID of the edge to select
	 */
	private void setSelectedEdgeID(String selectedEdgeID) {
		this.selectedEdgeID = selectedEdgeID;
	}

	/**
	 * Selects the Node with the given ID, resets Edge selection.
	 * 
	 * @param nodeID
	 *            the ID of the Node to select
	 */
	public void selectNode(String nodeID) {
		if (nodeID != null && g.getNode(nodeID) != null) {

			// set last selected node color to null
			if (getSelectedNodeID() != null)
				g.getNode(getSelectedNodeID()).changeAttribute("ui.style", "fill-color: #000000; size: 10px;");

			// set last selected edge color to black
			else if (getSelectedEdgeID() != null)
				g.getEdge(getSelectedEdgeID()).changeAttribute("ui.style", "fill-color: #000000;");

			setSelectedNodeID(nodeID);
			setSelectedEdgeID(null);

			// set selected node color to red
			g.getNode(nodeID).changeAttribute("ui.style", "fill-color: #FF0000; size: 15px;");
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
			// Set last selected Edge Color to Black
			if (getSelectedEdgeID() != null)
				g.getEdge(getSelectedEdgeID()).changeAttribute("ui.style", "fill-color: #000000;");

			// Set last selected Node color to black
			else if (getSelectedNodeID() != null)
				g.getNode(getSelectedNodeID()).changeAttribute("ui.style", "fill-color: #000000; size: 10px;");

			setSelectedNodeID(null);
			setSelectedEdgeID(edgeID);

			// set selected edge color to red
			g.getEdge(getSelectedEdgeID()).changeAttribute("ui.style", "fill-color: #FF0000;");
		}
	}

	/**
	 * Deselect any currently selected nodes or edges.
	 */
	// TODO remove selection style & call this before save
	public void deselect() {
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

	public ViewerPipe getFromViewer() {
		return fromViewer;
	}

	public void pumpIt() {
		fromViewer.pump();
	}

	@Override
	public String toString() {
		return "Visualizer for Graph \"" + g.getId() + "\"";
	}

	/**
	 * @return the currentPath
	 */
	public String getCurrentPath() {
		return currentPath;
	}

	/**
	 * @param currentPath
	 *            the currentPath to set
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
			attributes.put(s, deletedNode.getAttribute(s));
		}
		g.addEdge(e.getId(), (Node) e.getSourceNode(), (Node) e.getTargetNode());
		g.getEdge(e.getId()).addAttributes(attributes);
	}

	/**
	 * Adds a <b>Copy</b> of the given Node to the graph. The Copy retains the
	 * ID and all attributes.
	 * 
	 * @param e
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
	 * 
	 * @return the minimum X-Coordinate in the Graph
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
			Debug.out(Double.toString(currentMin));
		}
		Debug.out(Double.toString(currentMin));
		return currentMin;
	}

	/**
	 * 
	 * @return the maximum X-Coordinate in the Graph
	 */
	public double getMaxX() {
		double currentMax = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMax < (Double) n.getAttribute("x")) {
				currentMax = (Double) n.getAttribute("x");
			}
			Debug.out(Double.toString(currentMax));
		}
		Debug.out(Double.toString(currentMax));
		return currentMax;
	}

	/**
	 * 
	 * @return the minimum Y-Coordinate in the Graph
	 */
	public double getMinY() {
		double currentMin = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMin > (Double) n.getAttribute("y")) {
				currentMin = (Double) n.getAttribute("y");
			}
			Debug.out(Double.toString(currentMin));
		}
		Debug.out(Double.toString(currentMin));
		return currentMin;
	}

	/**
	 * 
	 * @return the maximum Y-Coordinate in the Graph
	 */
	public double getMaxY() {
		double currentMax = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = g.getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMax > (Double) n.getAttribute("y")) {
				currentMax = (Double) n.getAttribute("y");
			}
			Debug.out(Double.toString(currentMax));
		}
		Debug.out(Double.toString(currentMax));
		return currentMax;
	}

	/**
	 * Converts the coordinates of the Nodes to a saveable and uniform way
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
	 * converts the weight property into a label to display on the Graph removes
	 * all labels if that option is set
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
	 * @return the stylesheet
	 */
	public String getStylesheet() {
		return stylesheet;
	}

	/**
	 * @param stylesheet
	 *            the stylesheet to set
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
		String completeStylesheet = stylesheet;
		completeStylesheet = completeStylesheet.concat(OptionsManager.getNodeGraphics());
		completeStylesheet = completeStylesheet.concat(OptionsManager.getLayerStyle((Layer) g.getAttribute("layer")));
		g.addAttribute("ui.stylesheet", completeStylesheet);
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when an Edge is added
	 * 
	 * @param e
	 *            the EdgeCreatedListener
	 */
	public void addEdgeCreatedListener(EdgeCreatedListener e) {
		((MyGraph) g).addEdgeCreatedListener(e);
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when a Node is added
	 * 
	 * @param e
	 *            the NodeCreatedListener
	 */
	public void addNodeCreatedListener(NodeCreatedListener n) {
		((MyGraph) g).addNodeCreatedListener(n);
	}

}
