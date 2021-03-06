package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AbstractNode;
import org.graphstream.graph.implementations.SingleGraph;

import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.OptionsManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.ToolboxManager;
import de.tu_darmstadt.informatik.tk.scopviz.ui.css.CSSManager;

/**
 * Our own Class to extend GraphStreams Graph with our own Functionality.
 * 
 * @author Jan Enders (jan.enders@stud.tu-darmstadt.de)
 * @version 1.1
 * 
 */
public class MyGraph extends SingleGraph {

	/** List of all Edge Creation listeners. */
	private LinkedList<EdgeCreatedListener> allEdgeListeners = new LinkedList<EdgeCreatedListener>();
	/** List of all Node Creation listeners. */
	private LinkedList<NodeCreatedListener> allNodeListeners = new LinkedList<NodeCreatedListener>();

	private boolean composite = false;

	private LinkedList<MyGraph> children = new LinkedList<MyGraph>();

	/**
	 * Creates an empty graph with strict checking and without auto-creation.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 */
	public MyGraph(final String id) {
		super(id);
		setMyFactory();
	}

	/**
	 * Creates an empty graph with default edge and node capacity.
	 * 
	 * @param id
	 *            Unique identifier of the graph
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 */
	public MyGraph(final String id, final boolean strictChecking, final boolean autoCreate) {
		super(id, strictChecking, autoCreate);
		setMyFactory();
	}

	/**
	 * Creates an empty graph.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are
	 *            automatically created when referenced when creating a edge,
	 *            even if not yet inserted in the graph.
	 * @param initialNodeCapacity
	 *            Initial capacity of the node storage data structures. Use this
	 *            if you know the approximate maximum number of nodes of the
	 *            graph. The graph can grow beyond this limit, but storage
	 *            reallocation is expensive operation.
	 * @param initialEdgeCapacity
	 *            Initial capacity of the edge storage data structures. Use this
	 *            if you know the approximate maximum number of edges of the
	 *            graph. The graph can grow beyond this limit, but storage
	 *            reallocation is expensive operation.
	 */
	public MyGraph(final String id, final boolean strictChecking, final boolean autoCreate,
			final int initialNodeCapacity, final int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity, initialEdgeCapacity);
		setMyFactory();
	}

	/**
	 * sets Factories to MyNode and MyEdge
	 */
	private void setMyFactory() {
		setNodeFactory(new NodeFactory<MyNode>() {
			@Override
			public MyNode newInstance(String id, Graph graph) {
				return new MyNode((AbstractGraph) graph, id);
			}
		});
		setEdgeFactory(new EdgeFactory<MyEdge>() {
			@Override
			public MyEdge newInstance(String id, Node src, Node dst, boolean directed) {
				return new MyEdge(id, (AbstractNode) src, (AbstractNode) dst, directed);
			}
		});
	}

	/**
	 * adds the given Listener to the Graph all listeners will be notified when
	 * an Edge is created.
	 * 
	 * @param e
	 *            the listener that has to be added
	 */
	public void addEdgeCreatedListener(EdgeCreatedListener e) {
		allEdgeListeners.add(e);
	}

	/**
	 * Notifies all added EdgeCreatedListeners.
	 * 
	 * @param e
	 *            the Edge that was just created
	 */
	private void edgeCreatedNotify(Edge e) {
		boolean doWeight = Layer.UNDERLAY.equals(this.getAttribute("layer")) && (e.getAttribute("weight") == null
				|| (e.getAttribute("weight") != null && (OptionsManager.getDefaultWeight() == Main.getInstance()
						.convertAttributeTypes(e.getAttribute("weight"), new Double(0.0)))));
		if (doWeight) {
			ToolboxManager.createWeightDialog((MyEdge) e);
		}
		for (EdgeCreatedListener list : allEdgeListeners) {
			list.edgeCreated((MyEdge) e, id);
		}
	}

	/**
	 * adds the given Listener to the Graph all listeners will be notified when
	 * a Node is created.
	 * 
	 * @param n
	 *            the listener that has to be added
	 */
	public void addNodeCreatedListener(NodeCreatedListener n) {
		allNodeListeners.add(n);
	}

	/**
	 * Notifies all added NodeCreatedListener. also sets defaults
	 * 
	 * @param n
	 *            the Node that was just created
	 */
	private void nodeCreatedNotify(Node n) {
		GraphHelper.setAllDefaults(this);
		for (NodeCreatedListener list : allNodeListeners) {
			list.nodeCreated((MyNode) n, id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, int index1, int index2) {
		T e = super.addEdge(id, index1, index2);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, Node node1, Node node2) {
		T e = super.addEdge(id, node1, node2);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, String node1, String node2) {
		T e = super.addEdge(id, node1, node2);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex, boolean directed) {
		T e = super.addEdge(id, fromIndex, toIndex, directed);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, Node from, Node to, boolean directed) {
		T e = super.addEdge(id, from, to, directed);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, String from, String to, boolean directed) {
		T e = super.addEdge(id, from, to, directed);
		if (e instanceof MyEdge)
			CSSManager.addCSSAble((MyEdge) e);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Node> T addNode(String id) {
		T n = super.addNode(id);
		if (n instanceof MyNode)
			CSSManager.addCSSAble((MyNode) n);
		nodeCreatedNotify(n);
		return n;
	}

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		super.nodeAdded(sourceId, timeId, nodeId);
	}

	/**
	 * Returns the smallest X Coordinate of any Node in the Graph.
	 * 
	 * @return the smallest X Coordinate in the Graph
	 */
	public double getMinX() {
		double currentMin = Double.MAX_VALUE;
		Node n = null;
		Iterator<Node> allNodes = getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMin > (Double) n.getAttribute("x")) {
				currentMin = (Double) n.getAttribute("x");
			}
		}
		if (currentMin == Double.MAX_VALUE) {
			return 0;
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
		Iterator<Node> allNodes = getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("x") && currentMax < (Double) n.getAttribute("x")) {
				currentMax = (Double) n.getAttribute("x");
			}
		}
		if (currentMax == Double.MIN_VALUE) {
			return 0;
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
		Iterator<Node> allNodes = getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("y") && currentMin > (Double) n.getAttribute("y")) {
				currentMin = (Double) n.getAttribute("y");
			}
		}
		if (currentMin == Double.MAX_VALUE) {
			return 0;
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
		Iterator<Node> allNodes = getNodeIterator();

		while (allNodes.hasNext()) {
			n = allNodes.next();
			if (n.hasAttribute("y") && currentMax < (Double) n.getAttribute("y")) {
				currentMax = (Double) n.getAttribute("y");
			}
		}
		if (currentMax == Double.MIN_VALUE) {
			return 0;
		}
		return currentMax;
	}

	public void addSubGraph(MyGraph g) {
		composite = true;
		children.add(g);
	}

	public boolean isComposite() {
		return composite;
	}

	public LinkedList<MyGraph> getAllSubGraphs() {
		LinkedList<MyGraph> result = new LinkedList<MyGraph>();
		result.add(this);
		for (MyGraph g : children) {
			result.addAll(g.getAllSubGraphs());
		}
		return result;
	}

	public LinkedList<MyGraph> getChildren() {
		return children;
	}
}
