package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.graphstream.graph.Element;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.CreationMode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.GraphDisplayManager;
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

	/** String for the processing enabled type of node. */
	public static final String UI_CLASS_PROCESSING_ENABLED = "procEn";

	/** The Graph this instance of GraphManager manages. */
	protected MyGraph g;

	protected MyGraph activeSubGraph;

	/** The last Node that was deleted. */
	protected MyNode deletedNode;
	/** The last Edge that was deleted. */
	protected LinkedList<MyEdge> deletedEdges = new LinkedList<>();

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
	 * The Id of the Node that was last clicked.
	 */
	protected String lastClickedID;

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
		deselect();
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
	protected void deleteEdgesOfNode(final String id) {
		deselect();
		MyNode node = g.getNode(id);
		deletedEdges.removeAll(deletedEdges);
		deletedNode = null;
		MyEdge[] temp = new MyEdge[0];
		temp = g.getEdgeSet().toArray(temp);

		for (MyEdge e : temp) {
			if (e.getSourceNode().equals(node) || e.getTargetNode().equals(node)) {
				// adds the Edge to the list of deleted Edges and remove sit
				// from the Graph
				deletedEdges.add(g.removeEdge(e));
			}
		}
		GraphHelper.propagateElementDeletion(g, deletedEdges);
	}

	/**
	 * Undoes the last deleting operation on the given Graph. Deleting
	 * operations are: deleteNode, deleteEdge and deleteEdgesOfNode. Only undoes
	 * the last deleting operation even if that operation didn't change the
	 * Graph
	 */
	public void undelete() {
		String newId = "";
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		if (deletedNode != null) {
			for (String s : deletedNode.getAttributeKeySet()) {
				attributes.put(s, deletedNode.getAttribute(s));
			}
			newId = Main.getInstance().getUnusedID();
			g.addNode(newId);
			g.getNode(newId).addAttributes(attributes);
			String origElement = GraphHelper.propagateElementUndeletion(g, deletedNode, null);
			if (origElement != null) {
				g.getNode(newId).addAttribute("originalElement", origElement);
			}
		}

		for (MyEdge e : deletedEdges) {
			String sourceId = null;
			String targetId = null;
			attributes = new HashMap<String, Object>();
			for (String s : e.getAttributeKeySet()) {
				attributes.put(s, e.getAttribute(s));
			}
			String id = Main.getInstance().getUnusedID();
			if (deletedNode != null) {
				sourceId = (e.getSourceNode().getId().equals(deletedNode.getId())) ? newId : e.getSourceNode().getId();
				targetId = (e.getTargetNode().getId().equals(deletedNode.getId())) ? newId : e.getTargetNode().getId();
			} else {
				sourceId = e.getSourceNode().getId();
				targetId = e.getTargetNode().getId();

			}
			g.addEdge(id, sourceId, targetId, e.isDirected());
			g.getEdge(id).addAttributes(attributes);
			String origElement = GraphHelper.propagateElementUndeletion(g, e,
					g.getNode(newId).getAttribute("originalElement"));
			if (origElement != null) {
				g.getEdge(id).addAttribute("originalElement", origElement);
			}

		}

		deletedEdges = new LinkedList<>();
		deletedNode = null;
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

			MyNode n = g.getNode(nodeID);
			n.addCSSClass("selected");
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

			g.<MyEdge>getEdge(edgeID).addCSSClass("selected");
			PropertiesManager.setItemsProperties();
		}
	}

	/**
	 * Deselect any currently selected nodes or edges.
	 */
	// TODO call this before save
	protected void deselect() {
		// Set last selected Edge Color to Black
		if (getSelectedEdgeID() != null && g.getEdge(getSelectedEdgeID()) != null) {
			g.<MyEdge>getEdge(getSelectedEdgeID()).removeCSSClass("selected");
		}
		// Set last selected Node color to black
		else if (getSelectedNodeID() != null && g.getNode(getSelectedNodeID()) != null) {
			g.<MyNode>getNode(getSelectedNodeID()).removeCSSClass("selected");
		}
		PropertiesManager.setItemsProperties();
		this.selectedNodeID = null;
		this.selectedEdgeID = null;
	}

	/**
	 * Returns a reference to the Graph object managed by this visualizer.
	 * 
	 * @return the graph
	 */
	public MyGraph getGraph() {
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
	public void addEdge(MyEdge e) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : e.getAttributeKeySet()) {
			attributes.put(s, e.getAttribute(s));
		}
		g.addEdge(e.getId(), (MyNode) e.getSourceNode(), (MyNode) e.getTargetNode(), e.isDirected());
		g.getEdge(e.getId()).addAttributes(attributes);

		if (activeSubGraph != null) {
			activeSubGraph.addEdge(e.getId(), (MyNode) e.getSourceNode(), (MyNode) e.getTargetNode(), e.isDirected());
			activeSubGraph.getEdge(e.getId()).addAttributes(attributes);
			g.getEdge(e.getId()).addAttribute("originalElement", activeSubGraph.getId() + "+#" + e.getId());
		}
	}

	/**
	 * Adds a <b>Copy</b> of the given Node to the graph. The Copy retains the
	 * ID and all attributes.
	 * 
	 * @param n
	 *            the Node to be added to the graph
	 */
	public void addNode(MyNode n) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : n.getAttributeKeySet()) {
			attributes.put(s, n.getAttribute(s));
		}
		g.addNode(n.getId());
		g.getNode(n.getId()).addAttributes(attributes);

		if (activeSubGraph != null) {
			activeSubGraph.addNode(n.getId());
			activeSubGraph.getNode(n.getId()).addAttributes(attributes);
			g.getNode(n.getId()).addAttribute("originalElement", activeSubGraph.getId() + "+#" + n.getId());
		}
	}

	/**
	 * Returns the smallest X Coordinate of any Node in the Graph.
	 * 
	 * @return the smallest X Coordinate in the Graph
	 */
	public double getMinX() {
		return g.getMinX();
	}

	/**
	 * Returns the biggest X Coordinate of any Node in the Graph.
	 * 
	 * @return the biggest X Coordinate in the Graph
	 */
	public double getMaxX() {
		return g.getMaxX();
	}

	/**
	 * Returns the smallest Y Coordinate of any Node in the Graph.
	 * 
	 * @return the smallest Y Coordinate in the Graph
	 */
	public double getMinY() {
		return g.getMinY();
	}

	/**
	 * Returns the biggest Y Coordinate of any Node in the Graph.
	 * 
	 * @return the biggest Y Coordinate in the Graph
	 */
	public double getMaxY() {
		return g.getMaxY();
	}

	/**
	 * Sets the Stylesheet to be used by the Graph.
	 */
	public void setStylesheet() {
		g.addAttribute("ui.stylesheet", "edge{text-offset: 4px,-4px;}");
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when an Edge is added.
	 * 
	 * @param e
	 *            the EdgeCreatedListener
	 */
	public void addEdgeCreatedListener(EdgeCreatedListener e) {
		g.addEdgeCreatedListener(e);
	}

	/**
	 * adds the given listener to the underlying graph the listener will be
	 * notified, when a Node is added.
	 * 
	 * @param n
	 *            the NodeCreatedListener
	 */
	public void addNodeCreatedListener(NodeCreatedListener n) {
		g.addNodeCreatedListener(n);
	}

	/**
	 * Sets typeofNode as the ui.class of all Nodes.
	 * 
	 */
	public void convertUiClass() {
		Collection<MyNode> allNodes = g.getNodeSet();
		for (MyNode n : allNodes) {
			if (n.hasAttribute("typeofNode")) {
				n.addAttribute("ui.class", n.getAttribute("typeofNode").toString());
			}
		}
	}

	/**
	 * Create Edges based on CreateMode.
	 * 
	 * @param id
	 *            The ID for the newly created Edge
	 */
	public void createEdges(String id) {
		switch (Main.getInstance().getCreationMode()) {
		case CREATE_DIRECTED_EDGE:
		case CREATE_UNDIRECTED_EDGE:
			if (lastClickedID == null) {
				lastClickedID = id;
				if (!selectNodeForEdgeCreation(lastClickedID)) {
					lastClickedID = null;
				}
			} else if (id.equals(lastClickedID) || createEdge(id, lastClickedID)) {
				deselectNodesAfterEdgeCreation(lastClickedID);
				lastClickedID = null;
			}
			break;
		default:
			break;
		}
		PropertiesManager.setItemsProperties();
	}

	/**
	 * creates a edge between two nodes.
	 * 
	 * @author MW
	 * @param to
	 *            ID of the destination node
	 * @param from
	 *            ID of the origin node
	 * @return true if the edge was created. false otherwise
	 */
	protected boolean createEdge(String to, String from) {
		if (getGraph().getNode(from).hasEdgeBetween(to))
			return false;
		String newID = Main.getInstance().getUnusedID();

		if (Main.getInstance().getCreationMode() == CreationMode.CREATE_DIRECTED_EDGE) {
			getGraph().addEdge(newID, from, to, true);
			Debug.out("Created an directed edge with Id " + newID + " between " + from + " and " + to);
		} else {
			getGraph().addEdge(newID, from, to);
			Debug.out("Created an undirected edge with Id " + newID + " between " + from + " and " + to);
		}

		selectEdge(newID);

		return true;
	}

	/**
	 * Selects a Node as the starting point for creating a new Edge.
	 * 
	 * @param nodeID
	 *            the ID of the Node to select
	 */
	protected boolean selectNodeForEdgeCreation(String nodeID) {
		deselect();
		MyNode n = getGraph().getNode(nodeID);
		if (!hasClass(n, UI_CLASS_PROCESSING_ENABLED) || !GraphDisplayManager.getCurrentLayer().equals(Layer.MAPPING)) {
			n.addCSSClass("selectedForEdgeCreation");
		}
		return true;
	}

	/**
	 * Reset the Selection of the Node after Edge has been successfully created.
	 * 
	 * @param nodeID
	 *            the Id of the node to deselect.
	 */
	protected void deselectNodesAfterEdgeCreation(String nodeID) {
		MyNode n = getGraph().getNode(nodeID);
		if (n == null) {
			return;
		}
		if (!hasClass(n, UI_CLASS_PROCESSING_ENABLED) || !GraphDisplayManager.getCurrentLayer().equals(Layer.MAPPING)) {
			n.removeCSSClass("selectedForEdgeCreation");
		}
	}

	/**
	 * Resets the selction of the Node for Edge selection
	 */
	public void deselectEdgeCreationNodes() {
		if (lastClickedID != null)
			deselectNodesAfterEdgeCreation(lastClickedID);
		lastClickedID = null;
	}

	public void setActiveSubGraph(String id) {
		for (MyGraph subGraph : g.getAllSubGraphs()) {
			if (subGraph.getId().equals(id)) {
				activeSubGraph = subGraph;
				return;
			}
		}
	}

	protected boolean addClass(String id, String className) {
		Element e = getGraph().getEdge(id);
		if (e == null)
			e = getGraph().getNode(id);
		if (e == null)
			return false;
		String eClass = e.getAttribute("ui.class");
		if (eClass == null || eClass.equals(""))
			eClass = className;
		else if (!(eClass.equals(className) || eClass.startsWith(className.concat(", "))
				|| eClass.contains(", ".concat(className))))
			eClass = className.concat(", ").concat(eClass);

		e.addAttribute("ui.class", eClass);
		Debug.out("added " + className + ": " + eClass);
		return true;
	}

	protected boolean removeClass(String id, String className) {
		Element e = getGraph().getEdge(id);
		if (e == null)
			e = getGraph().getNode(id);
		if (e == null)
			return false;
		String eClass = e.getAttribute("ui.class");
		if (eClass == null || eClass.equals(className))
			eClass = "";
		else
			eClass = eClass.replace(className.concat(", "), "").replace(", ".concat(className), "");

		e.addAttribute("ui.class", eClass);
		Debug.out("removed " + className + ": " + eClass);
		return true;
	}

	protected boolean toggleClass(String id, String className) {
		Element e = getGraph().getEdge(id);
		if (e == null)
			e = getGraph().getNode(id);
		if (e == null)
			return false;
		String eClass = e.getAttribute("ui.class");
		if (eClass == null || !(eClass.equals(className) || eClass.startsWith(className.concat(", "))
				|| eClass.contains(", ".concat(className))))
			return addClass(id, className);
		return removeClass(id, className);
	}

	protected boolean hasClass(String id, String className) {
		Element e = getGraph().getEdge(id);
		if (e == null)
			e = getGraph().getNode(id);
		if (e == null)
			return false;
		String eClass = e.getAttribute("ui.class");
		return (eClass != null && (eClass.equals(className) || eClass.startsWith(className.concat(", "))
				|| eClass.contains(", ".concat(className))));
	}

	protected boolean hasClass(MyEdge e, String className) {
		if (e == null)
			return false;
		String eClass = e.getAttribute("ui.class");
		return (eClass != null && (eClass.equals(className) || eClass.startsWith(className.concat(", "))
				|| eClass.contains(", ".concat(className))));
	}

	protected boolean hasClass(MyNode n, String className) {
		if (n == null)
			return false;
		String nClass = n.getAttribute("ui.class");
		/*
		 * TODO: nochmal angucken, wenn CSS Manager steht, ist gerade gehackt
		 * damit, Vorführung läuft. return (nClass != null &&
		 * (nClass.equals(className) ||
		 * nClass.startsWith(className.concat(", ")) ||
		 * nClass.contains(", ".concat(className))));
		 */
		return (nClass != null && nClass.contains(className));
	}
}
