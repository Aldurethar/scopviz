package de.tu_darmstadt.informatik.tk.scopviz.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.ui.MetricboxManager;

/**
 * Class extending GraphManager. Offers the possibility to merge an underlay and
 * an operator graph.
 * 
 * 
 * @author Matthias Wilhelm
 *
 */
public class MappingGraphManager extends GraphManager implements EdgeCreatedListener, NodeCreatedListener {
	public static final String UNDERLAY = "underlay";
	public static final String OPERATOR = "operator";

	public static final String ATTRIBUTE_KEY_PROCESS_NEED = "process-need";
	public static final String ATTRIBUTE_KEY_PROCESS_USE = "process-use";
	public static final String ATTRIBUTE_KEY_PROCESS_MAX = "process-max";
	public static final String ATTRIBUTE_KEY_MAPPING = "mapping";
	public static final String ATTRIBUTE_KEY_MAPPING_PARENT = "mapping-parent";
	public static final String ATTRIBUTE_KEY_MAPPING_PARENT_ID = "mapping-parent-id";
	public static final String UI_CLASS_MAPPING = "mapping";

	private static final double UNDERLAYER_MOVE_Y = 0;
	private static final double OPERATOR_MOVE_Y = 1.5;
	private static final double SCALE_WIDTH = 2;
	private static final double SCALE_HEIGHT = 1;

	/** Variables to keep track of new Nodes in the underlay graph */
	private boolean underlayNodesChanged = false;

	/** Variables to keep track of new Nodes in the operator graph */
	private boolean operatorNodesChanged = false;

	/** References to the underlay graph */
	GraphManager underlay;
	/** References to the operator graph */
	GraphManager operator;
	/** Map to store the id of the underlay and operator graphs IDs */
	HashMap<String, String> parentsID;

	/**
	 * Creates a new manager for an empty graph. there is no need to check for
	 * unique ID's for nodes and edges.
	 * 
	 * @param graph
	 *            assumes a empty graph
	 * @param underlay
	 *            the underlay graph
	 * @param operator
	 *            the operator graph
	 */
	public MappingGraphManager(MyGraph graph, GraphManager underlay, GraphManager operator) {
		super(graph);

		underlay.deselect();
		operator.deselect();

		this.underlay = underlay;
		this.operator = operator;

		parentsID = new HashMap<>();
		parentsID.put(UNDERLAY, underlay.getGraph().getId());
		parentsID.put(OPERATOR, operator.getGraph().getId());

		Debug.out("Created a new Mapping");

		graph.addSubGraph(underlay.getGraph());
		graph.addSubGraph(operator.getGraph());
		mergeGraph(underlay, UNDERLAY, UNDERLAYER_MOVE_Y);
		mergeGraph(operator, OPERATOR, OPERATOR_MOVE_Y);
		autoMapSourcesAndSinks(underlay, operator);

		view.getCamera().resetView();
	}

	/**
	 * 
	 * @param g
	 */
	public void loadGraph(MyGraph g) {

		// reset used capacity to 0 for every procEnabled node
		for (Node n : this.g.getNodeSet()) {
			if (hasClass(n, UI_CLASS_PROCESSING_ENABLED)) {
				resetCapacity(n);
			}
		}

		// recreates every mapping edge to properly calculate capacities
		for (Edge e : g.getEdgeSet()) {
			if ((boolean) e.getAttribute(ATTRIBUTE_KEY_MAPPING)) {
				createEdge(e.getSourceNode().getId(), e.getTargetNode().getId());
			}
		}
	}

	/**
	 * Adds all nodes and edges of the given graph, adds a prefix to the ID of
	 * every node and edge and offsets the normalized coordinates in y
	 * direction.
	 * 
	 * @param gm
	 *            the graph to be added
	 * @param idPrefix
	 *            the prefix for the ID of every node and edge
	 * @param moveY
	 *            the offset of the y coordinate
	 */
	private void mergeGraph(GraphManager gm, String idPrefix, double moveY) {
		mergeNodes(gm, idPrefix, moveY);

		// Debug only
		int i = 0;

		for (Edge edge : gm.getGraph().getEdgeSet()) {
			addEdge(edge, idPrefix);

			// Debug only
			i++;
		}

		Debug.out("added " + i + " Edge" + (i == 1 ? "" : "s") + " from \"" + idPrefix + "\"");
	}

	/**
	 * Adds all nodes of the given graph, adds a prefix to the ID of every node
	 * and offsets the normalized coordinates in y direction.
	 * 
	 * @param gm
	 *            the graph to be added
	 * @param idPrefix
	 *            the prefix for the ID of every node
	 * @param moveY
	 *            the offset of the y coordinate
	 */
	private void mergeNodes(GraphManager gm, String idPrefix, double moveY) {
		// precalculate scale and offset to normalize x coordinates
		double maxX = gm.getMaxX();
		double minX = gm.getMinX();
		double scaleX = SCALE_WIDTH / (maxX - minX);
		double addX = -minX * scaleX;
		if (maxX == minX) {
			scaleX = 0;
			addX = -SCALE_WIDTH / 2;
		} else {
			scaleX = SCALE_WIDTH / (maxX - minX);
			addX = -minX * scaleX;
		}

		// precalculate scale and offset to normalize y coordinates
		double maxY = gm.getMaxY();
		double minY = gm.getMinY();
		double scaleY;
		double addY;
		if (maxY == minY) {
			scaleY = 0;
			addY = SCALE_HEIGHT / 2 + moveY;
		} else {
			scaleY = SCALE_HEIGHT / (maxY - minY);
			addY = -minY * scaleY + moveY;
		}

		// Debug only
		int i = 0;

		// loops through all nodes, adds them if they don't exist already and
		// normalizes their coordinates
		for (Node node : gm.getGraph().getNodeSet()) {
			// add node if it doesn't exist
			Node newNode = getGraph().getNode(idPrefix + node.getId());
			if (newNode == null) {
				addNode(node, idPrefix);
				newNode = getGraph().getNode(idPrefix + node.getId());

				// Debug only
				i++;
			}

			// normalize coordinates
			double[] n = Toolkit.nodePosition(node);
			double cX = n[0];
			double x = cX * scaleX + addX;
			double cY = n[1];
			double y = cY * scaleY + addY;
			newNode.changeAttribute("x", x);
			newNode.changeAttribute("y", y);

			if (hasClass(newNode, UI_CLASS_PROCESSING_ENABLED)) {
				initCapacity(newNode);
			}

		}

		Debug.out("added " + i + " Node" + (i == 1 ? "" : "s") + " from \"" + idPrefix + "\"");
	}

	/**
	 * Gets invoked by the GraphDisplayManager every time the mapping layer is
	 * loaded. Checks whether nodes have been added to the parent graphs
	 */
	public void activated() {
		if (underlayNodesChanged) {
			mergeNodes(underlay, UNDERLAY, UNDERLAYER_MOVE_Y);
			underlayNodesChanged = false;
		}

		if (operatorNodesChanged) {
			mergeNodes(operator, OPERATOR, OPERATOR_MOVE_Y);
			operatorNodesChanged = false;
		}
	}

	/**
	 * Checks whether the given graph is underlay or operator graph to this
	 * object or not.
	 * 
	 * @param gm
	 *            the graph to check
	 * @return true if the given graph is underlay or operator graph to this
	 *         graph. false otherwise
	 */
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

		attributes.put(ATTRIBUTE_KEY_MAPPING, false);
		attributes.put(ATTRIBUTE_KEY_MAPPING_PARENT, idPrefix);
		attributes.put(ATTRIBUTE_KEY_MAPPING_PARENT_ID, parentsID.get(idPrefix));

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

		attributes.put(ATTRIBUTE_KEY_MAPPING_PARENT, idPrefix);
		attributes.put(ATTRIBUTE_KEY_MAPPING_PARENT_ID, parentsID.get(idPrefix));

		g.addNode(idPrefix + n.getId());
		g.getNode(idPrefix + n.getId()).addAttributes(attributes);
	}

	@Override
	public void nodeCreated(Node n, String graphID) {
		if (graphID.equals(underlay.getGraph().getId()))
			underlayNodesChanged = true;
		else if (graphID.equals(operator.getGraph().getId()))
			operatorNodesChanged = true;
	}

	@Override
	public void edgeCreated(Edge e, String graphID) {
		if (graphID.equals(underlay.getGraph().getId()))
			addEdge(e, UNDERLAY);
		else if (graphID.equals(operator.getGraph().getId()))
			addEdge(e, OPERATOR);
	}

	@Override
	public void createEdges(String id) {
		super.createEdges(id);

		if (lastClickedID != null) {
			Double need = g.getNode(lastClickedID).getAttribute(ATTRIBUTE_KEY_PROCESS_NEED);
			if (need != null)
				for (Node n : g.getNodeSet())
					if (hasClass(n, UI_CLASS_PROCESSING_ENABLED))
						showExpectedCapacity(n, need);
		} else {
			for (Node n : g.getNodeSet())
				if (hasClass(n, UI_CLASS_PROCESSING_ENABLED))
					showExpectedCapacity(n, 0);
		}
	}

	/**
	 * checks whether the Node can handle the load first.<br/>
	 * creates a edge between to nodes
	 */
	@Override
	public boolean createEdge(String to, String from) {
		Node fromNode = getGraph().getNode(from);
		Node toNode = getGraph().getNode(to);

		if (fromNode.hasEdgeBetween(to))
			return false;

		for (Edge e : fromNode.getEdgeSet()) {
			Boolean mapped = e.getAttribute("mapping");
			if (mapped != null && mapped)
				return false;
		}

		String fromParent = fromNode.getAttribute(ATTRIBUTE_KEY_MAPPING_PARENT);
		String toParent = toNode.getAttribute(ATTRIBUTE_KEY_MAPPING_PARENT);

		if (fromParent == null || toParent == null)
			return false;
		if (fromParent.equals(toParent)) {
			deselectNodesAfterEdgeCreation(lastClickedID);
			lastClickedID = to;
			selectNodeForEdgeCreation(lastClickedID);
			return false;
		}

		String newID = Main.getInstance().getUnusedID();

		Edge e;

		Node underlayNode;
		Node operatorNode;
		if (fromParent.equals(UNDERLAY)) {
			underlayNode = fromNode;
			operatorNode = toNode;
		} else if (toParent.equals(UNDERLAY)) {
			underlayNode = toNode;
			operatorNode = fromNode;
		} else
			return false;

		// check if processing enabled node
		if (!hasClass(underlayNode, UI_CLASS_PROCESSING_ENABLED))
			return false;

		// check and update capacity
		if (!addMapping(underlayNode, operatorNode)){
			Debug.out("Could not place Mapping Edge due to insufficient capacity!", 2);
			return false;
		}
			

		e = getGraph().addEdge(newID, operatorNode, underlayNode, true);
		Debug.out("Created an directed edge with Id " + newID + " from " + operatorNode + " to " + underlayNode);

		e.addAttribute("ui.class", UI_CLASS_MAPPING);
		e.addAttribute(ATTRIBUTE_KEY_MAPPING, true);

		selectEdge(newID);
		MetricboxManager.updateMetrics();
		return true;
	}

	/**
	 * Initialize the pie chart for the given node. It uses the process_use and
	 * process_max values of the given node. If process_max is null or 0 it
	 * won't do anything. If process_use is null it will be initialized to 0.
	 * 
	 * @param underlayNode
	 *            The Node for which the pie chart should be initialized
	 */
	private void initCapacity(Node underlayNode) {
		Double used = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_USE);
		Double max = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_MAX);
		if (max == null || max == 0)
			return;
		if (used == null)
			used = new Double(0);
		double[] pieValues = { used / max, 0, 1 - used / max };
		underlayNode.setAttribute("ui.pie-values", pieValues);
		underlayNode.setAttribute(ATTRIBUTE_KEY_PROCESS_USE, used);
	}

	/**
	 * Resets the pie chart for the given node. If process_max is null or 0 it
	 * won't display anything. Process_use set to 0.
	 * 
	 * @param underlayNode
	 *            The Node for which the pie chart should be initialized
	 */
	private void resetCapacity(Node underlayNode) {
		Double used = new Double(0);
		underlayNode.setAttribute(ATTRIBUTE_KEY_PROCESS_USE, used);
		Double max = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_MAX);
		if (max == null || max == 0)
			return;
		double[] pieValues = { 0, 0, 1 };
		underlayNode.setAttribute("ui.pie-values", pieValues);
	}

	/**
	 * Checks and updates the Capacity for a procEn node. Tries to map the given
	 * operatorNode to the given underlayNode.
	 * 
	 * @param underlayNode
	 *            The underlayNode the operatorNode gets mapped to
	 * @param operatorNode
	 *            The operatorNode which gets mapped
	 * @return true if the mapping was successful. false otherwise.
	 */
	private boolean addMapping(Node underlayNode, Node operatorNode) {
		Double needed = operatorNode.getAttribute(ATTRIBUTE_KEY_PROCESS_NEED);
		if (needed == null)
			return true;
		return changeCapacity(underlayNode, needed);
	}

	/**
	 * Checks and updates the Capacity for a procEn node. Tries to unmap the
	 * given operatorNode to the given underlayNode.
	 * 
	 * @param underlayNode
	 *            The underlayNode the operatorNode gets mapped to
	 * @param operatorNode
	 *            The operatorNode which gets mapped
	 * @return true if the mapping was successful. false otherwise.
	 */
	private boolean removeMapping(Node underlayNode, Node operatorNode) {
		Double needed = operatorNode.getAttribute(ATTRIBUTE_KEY_PROCESS_NEED);
		if (needed == null)
			return true;
		return changeCapacity(underlayNode, -needed);
	}

	/**
	 * Checks and updates the Capacity for a procEn node. Tries to map the
	 * capacity to the given underlayNode.
	 * 
	 * @param underlayNode
	 *            The underlayNode which capacity gets updated
	 * @param capacity
	 *            The capacity. may be positive or negative
	 * @return true if the capacity change was successful. false otherwise.
	 */
	private boolean changeCapacity(Node underlayNode, double capacity) {
		Double needed = capacity;
		Double used = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_USE);
		Double max = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_MAX);
		if (needed == 0)
			return true;

		if (max == null || max == 0)
			if (needed > 0)
				return false;
		if (used == null)
			used = new Double(0);
		if (used + needed > max)
			return false;
		used += needed;
		double[] pieValues = { used / max, 0, 1 - used / max };
		underlayNode.setAttribute("ui.pie-values", pieValues);
		underlayNode.setAttribute(ATTRIBUTE_KEY_PROCESS_USE, used);
		return true;
	}

	/**
	 * Displays the capacity change to the node if the needed Cost is applied.
	 * 
	 * @param underlayNode
	 *            The node which gets updated
	 * @param need
	 *            the capacity change
	 */
	private void showExpectedCapacity(Node underlayNode, double need) {
		Double used = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_USE);
		Double max = underlayNode.getAttribute(ATTRIBUTE_KEY_PROCESS_MAX);
		if (max == null || max == 0)
			return;
		if (used == null)
			used = new Double(0);
		double[] pieValues = { used / max, 0, 1 - used / max, 0 };
		if (need + used > max) {
			pieValues[3] = pieValues[2];
			pieValues[2] = 0;
		} else {
			pieValues[1] = need / max;
			pieValues[2] -= need / max;
		}

		underlayNode.setAttribute("ui.pie-values", pieValues);
		underlayNode.setAttribute(ATTRIBUTE_KEY_PROCESS_USE, used);
	}

	@Override
	protected boolean selectNodeForEdgeCreation(String nodeID) {
		Node n = g.getNode(nodeID);
		String parent = n.getAttribute(ATTRIBUTE_KEY_MAPPING_PARENT);
		if (parent == null)
			return false;
		if (parent.equals(OPERATOR))
			return super.selectNodeForEdgeCreation(nodeID);
		if (hasClass(n, UI_CLASS_PROCESSING_ENABLED))
			return super.selectNodeForEdgeCreation(nodeID);
		return false;
	}

	@Override
	public void deleteEdge(final String id) {
		Edge e = g.getEdge(id);
		if ((boolean) e.getAttribute(ATTRIBUTE_KEY_MAPPING)) {
			Node operatorNode = e.getSourceNode();
			Node underlayNode = e.getTargetNode();
			removeMapping(underlayNode, operatorNode);
			super.deleteEdge(id);
		}
	}

	@Override
	public void deleteNode(String id) {
		Debug.out("default delete Node prevented");
	}

	@Override
	public void undelete() {
		super.undelete();
		for (Edge e : deletedEdges) {
			if ((boolean) e.getAttribute(ATTRIBUTE_KEY_MAPPING))
				changeCapacity(e.getTargetNode(), e.getSourceNode().getAttribute(ATTRIBUTE_KEY_PROCESS_NEED));
		}
	}

	private void autoMapSourcesAndSinks(GraphManager underlay, GraphManager operator) {
		for (Node operatorNode : getOperatorNodeSet()) {
			if (operatorNode.getAttribute("typeofNode").toString().equals("source")) {
				for (Node underlayNode : getUnderlayNodeSet()) {
					if (operatorNode.getAttribute("identifier") != null && operatorNode.getAttribute("identifier")
							.equals(underlayNode.getAttribute("identifier"))) {
						String newID = Main.getInstance().getUnusedID(this);
						Edge e = getGraph().addEdge(newID, operatorNode, underlayNode, true);
						Debug.out("Created an directed edge with Id " + newID + " from " + operatorNode + " to "
								+ underlayNode);

						e.addAttribute("ui.class", UI_CLASS_MAPPING);
						e.addAttribute(ATTRIBUTE_KEY_MAPPING, true);
					}
				}
			} else if (operatorNode.getAttribute("typeofNode").equals("sink")) {
				for (Node underlayNode : getUnderlayNodeSet()) {
					String identifier = operatorNode.getAttribute("identifier");
					if (identifier != null && identifier.equals(underlayNode.getAttribute("identifier"))) {
						String newID = Main.getInstance().getUnusedID(this);
						Edge e = getGraph().addEdge(newID, operatorNode, underlayNode, true);
						Debug.out("Created an directed edge with Id " + newID + " from " + operatorNode + " to "
								+ underlayNode);

						e.addAttribute("ui.class", UI_CLASS_MAPPING);
						e.addAttribute(ATTRIBUTE_KEY_MAPPING, true);
					}
				}
			}
		}
	}

	private Collection<Node> getOperatorNodeSet() {
		LinkedList<Node> result = new LinkedList<Node>();
		for (Node n : getGraph().getNodeSet()) {
			if (n.getAttribute(ATTRIBUTE_KEY_MAPPING_PARENT) == OPERATOR) {
				result.add(n);
			}
		}
		return result;
	}

	private Collection<Node> getUnderlayNodeSet() {
		LinkedList<Node> result = new LinkedList<Node>();
		for (Node n : getGraph().getNodeSet()) {
			if (n.getAttribute(ATTRIBUTE_KEY_MAPPING_PARENT) == UNDERLAY) {
				result.add(n);
			}
		}
		return result;
	}

}