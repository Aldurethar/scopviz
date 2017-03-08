package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

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

	/**
	 * Creates an empty graph with strict checking and without auto-creation.
	 * 
	 * @param id
	 *            Unique identifier of the graph.
	 */
	public MyGraph(final String id) {
		super(id);
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
		for (EdgeCreatedListener list : allEdgeListeners) {
			list.edgeCreated(e, id);
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
	 * Notifies all added NodeCreatedListener.
	 * 
	 * @param n
	 *            the Edge that was just created
	 */
	private void nodeCreatedNotify(Node n) {
		for (NodeCreatedListener list : allNodeListeners) {
			list.nodeCreated(n, id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, int index1, int index2) {
		T e = super.addEdge(id, index1, index2);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, Node node1, Node node2) {
		T e = super.addEdge(id, node1, node2);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, String node1, String node2) {
		T e = super.addEdge(id, node1, node2);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, int fromIndex, int toIndex, boolean directed) {
		T e = super.addEdge(id, fromIndex, toIndex, directed);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, Node from, Node to, boolean directed) {
		T e = super.addEdge(id, from, to, directed);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Edge> T addEdge(String id, String from, String to, boolean directed) {
		T e = super.addEdge(id, from, to, directed);
		edgeCreatedNotify(e);
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Node> T addNode(String id) {
		T n = super.addNode(id);
		nodeCreatedNotify(n);
		return n;
	}
}
