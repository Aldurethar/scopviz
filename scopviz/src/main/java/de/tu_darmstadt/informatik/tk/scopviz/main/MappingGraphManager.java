package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.HashMap;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

public class MappingGraphManager extends GraphManager implements EdgeCreatedListener, NodeCreatedListener {
	public static final String UNDERLAYER_PREFIX = "underlay";
	public static final String OPERATOR_PREFIX = "operator";
	private static final double UNDERLAYER_MOVE_Y = 0;
	private static final double OPERATOR_MOVE_Y = 1.5;
	private boolean underlayNodesChanged = false;
	private boolean operatorNodesChanged = false;

	GraphManager underlay, operator;

	public MappingGraphManager(MyGraph graph, GraphManager underlay, GraphManager operator) {
		super(graph);
		this.underlay = underlay;
		this.operator = operator;
		Debug.out("Created a new Mapping");
		mergeGraph(underlay, UNDERLAYER_PREFIX, UNDERLAYER_MOVE_Y);
		mergeGraph(operator, OPERATOR_PREFIX, OPERATOR_MOVE_Y);
		view.getCamera().resetView();
	}

	private void mergeGraph(GraphManager gm, String idPrefix, double moveY) {

		mergeNodes(gm, idPrefix, moveY);
		int i = 0;
		for (Edge edge : gm.getGraph().getEdgeSet()) {
			addEdge(edge, idPrefix);
			i++;
		}
		Debug.out("added " + i + " Edge" + (i == 1 ? "" : "s") + " from \"" + idPrefix + "\"");
	}

	private void mergeNodes(GraphManager gm, String idPrefix, double moveY) {
		int i = 0;
		double maxX = gm.getMaxX();
		double minX = gm.getMinX();
		double scaleX = 1 / (maxX - minX);
		double addX = -minX * scaleX;
		double maxY = gm.getMaxY();
		double minY = gm.getMinY();
		double scaleY = 1 / (maxY - minY);
		double addY = -minY * scaleY;
		for (Node node : gm.getGraph().getNodeSet()) {
			Node newNode = getGraph().getNode(idPrefix + node.getId());
			if (newNode == null) {
				addNode(node, idPrefix);
				newNode = getGraph().getNode(idPrefix + node.getId());
				i++;
			}
			double[] n = Toolkit.nodePosition(node);
			double cX = n[0];
			double x = cX * scaleX + addX;
			double cY = n[1];
			double y = cY * scaleY + addY + moveY;
			newNode.changeAttribute("x", x);
			newNode.changeAttribute("y", y);
		}
		Debug.out("added " + i + " Node" + (i == 1 ? "" : "s") + " from \"" + idPrefix + "\"");
	}

	public void activated() {
		if (underlayNodesChanged) {
			mergeNodes(underlay, UNDERLAYER_PREFIX, UNDERLAYER_MOVE_Y);
			underlayNodesChanged = false;
		}
		if (operatorNodesChanged) {
			mergeNodes(operator, OPERATOR_PREFIX, OPERATOR_MOVE_Y);
			operatorNodesChanged = false;
		}
	}

	public boolean hasGraphManagerAsParent(GraphManager gm) {
		return (underlay.getGraph().getId().equals(gm.getGraph().getId()))
				|| (operator.getGraph().getId().equals(gm.getGraph().getId()));
	}

	/**
	 * Adds a <b>Copy</b> of the given Edge to the graph. The Copy retains the
	 * ID and all attributes but adds the ID prefix in front of the all old IDs.
	 * 
	 * @param e
	 *            the Edge to be added to the graph
	 * @param idPrefix
	 *            the String to be added as prefix to the ID
	 */
	private void addEdge(Edge e, String idPrefix) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : e.getAttributeKeySet()) {
			attributes.put(s, e.getAttribute(s));
		}
		g.addEdge(idPrefix + e.getId(), idPrefix + e.getSourceNode().getId(), idPrefix + e.getTargetNode().getId());
		g.getEdge(idPrefix + e.getId()).addAttributes(attributes);
	}

	@Override
	public void addNode(Node n) {
		// This function mustn't be called.
		Debug.out("Someone called addNode(Node n) with a MappingGraphManager");
	}

	/**
	 * Adds a <b>Copy</b> of the given Node to the graph. The Copy retains the
	 * ID and all attributes but adds the ID prefix in front of the old ID.
	 * 
	 * @param n
	 *            the Node to be added to the graph
	 * @param idPrefix
	 *            the String to be added as prefix to the ID
	 */
	private void addNode(Node n, String idPrefix) {
		HashMap<String, Object> attributes = new HashMap<>();

		for (String s : n.getAttributeKeySet()) {
			attributes.put(s, n.getAttribute(s));
		}
		g.addNode(idPrefix + n.getId());
		g.getNode(idPrefix + n.getId()).addAttributes(attributes);
	}

	@Override
	public void nodeCreated(Node n, String graphID) {
		Debug.out("Node " + n + " was added to Graph " + graphID);
		if (graphID.equals(underlay.getGraph().getId()))
			underlayNodesChanged = true;
		else
			operatorNodesChanged = true;
	}

	@Override
	public void edgeCreated(Edge e, String graphID) {
		Debug.out("Edge " + e + " was added to Graph " + graphID);
		if (graphID.equals(underlay.getGraph().getId()))
			addEdge(e, UNDERLAYER_PREFIX);
		else
			addEdge(e, OPERATOR_PREFIX);
	}
}