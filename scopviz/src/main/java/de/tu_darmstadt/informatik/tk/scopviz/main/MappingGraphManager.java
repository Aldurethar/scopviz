package de.tu_darmstadt.informatik.tk.scopviz.main;

import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;

public class MappingGraphManager extends GraphManager {
	public static final String UNDERLAYER_PREFIX = "underlay";
	public static final String OPERATOR_PREFIX = "operator";

	GraphManager underlay, operator;

	public MappingGraphManager(Graph graph, GraphManager underlay, GraphManager operator) {
		super(graph);
		this.underlay = underlay;
		this.operator = operator;

		mergeGraph(underlay, UNDERLAYER_PREFIX);
		mergeGraph(operator, OPERATOR_PREFIX);
	}

	private void mergeGraph(GraphManager gm, String idPrefix) {
		for (Node node : gm.getGraph().getNodeSet()) {
			addNode(node, idPrefix);
		}
		for (Edge edge : gm.getGraph().getEdgeSet()) {
			addEdge(edge, idPrefix);
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
		g.getEdge(e.getId()).addAttributes(attributes);
	}
	
	@Override
	public void addNode(Node n){
		//This function mustn't be called.
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
		g.getNode(n.getId()).addAttributes(attributes);
	}
}