package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Toolkit;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyEdge;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyNode;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.util.Pair;

/**
 * Class to compute the communication cost metric. The Metric is defined as the
 * sum of the network traversal costs for each hop in the longest path in the
 * operator graph. wARNING: This might not work fully as intended with multiple
 * Operator graphs!
 * 
 * @author Jan Enders
 * @version 0.9
 *
 */
// TODO: make this work well with multiple operator graphs
public class CommunicationCostMetric implements ScopvizGraphMetric {

	/**
	 * Message to show if not all Operator nodes have been placed onto the
	 * underlay
	 */
	private static final Pair<String, String> ERROR_MESSAGE = new Pair<String, String>("Warning",
			"Not all Nodes have a valid Mapping");

	/** Flag for when an error occurs during computation. */
	// TODO: this is not yet being used for output and never reset
	private boolean error = false;

	@Override
	public boolean isSetupRequired() {
		return false;
	}

	@Override
	public String getName() {
		return "Communication Cost";
	}

	@Override
	public void setup() {
		// No Setup required.
	}

	@Override
	public LinkedList<Pair<String, String>> calculate(MyGraph g) {
		LinkedList<Pair<String, String>> results = new LinkedList<Pair<String, String>>();

		if (error) {
			error = false;
			results.add(ERROR_MESSAGE);
		}

		MyGraph operator = new MyGraph("opWithTime");
		for (MyNode n : g.<MyNode>getNodeSet()) {
			if (n.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING_PARENT) == MappingGraphManager.OPERATOR) {
				operator.addNode(n.getId());
			}

		}
		for (MyEdge e : g.<MyEdge>getEdgeSet()) {
			if (e.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING_PARENT) == MappingGraphManager.OPERATOR) {
				String newID = e.getId();
				double cost = computeCost(e.getNode0(), e.getNode1(), g);
				operator.addEdge(newID, e.getNode0().getId(), e.getNode1().getId(), true);
				operator.getEdge(newID).addAttribute("cost", cost);
			}
		}

		// TODO: not fully sure if the diameter Method does exactly what we
		// want, requires testing
		double communicationCost = Toolkit.diameter(operator, "cost", true);
		communicationCost = Math.round(communicationCost * 100) / 100;

		results.add(new Pair<String, String>("Overall Cost", "" + communicationCost));

		return results;
	}

	/**
	 * Compute the Network traversal cost for the Communication between two
	 * given operator nodes.
	 * 
	 * @param n1
	 *            The first operator node
	 * @param n2
	 *            The second operator node
	 * @param g
	 *            the combined mapping graph
	 * @return the cost
	 */
	private double computeCost(MyNode n1, MyNode n2, MyGraph g) {
		double cost = 0;
		// find the underlay nodes that the operator nodes are mapped to
		LinkedList<MyEdge> mappingEdges = new LinkedList<MyEdge>(g.<MyEdge>getEdgeSet().stream()
				.filter(e -> (((Boolean) e.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING)) == true))
				.collect(Collectors.toList()));
		MyNode target1 = null;
		MyNode target2 = null;
		for (MyEdge e : mappingEdges) {
			if (e.getNode0() == n1) {
				target1 = e.getNode1();
			} else if (e.getNode0() == n2) {
				target2 = e.getNode1();
			}
		}
		// Error if not both operator nodes have a valid mapping
		if (target1 == null || target2 == null) {
			Debug.out("Could not find Mapping target for Operator Node " + n1.getId() + " or " + n2.getId());
			error = true;
		} else {
			// find shortest path between the two underlay nodes
			Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
			dijkstra.init(g);
			dijkstra.setSource(target1);
			dijkstra.compute();
			cost = dijkstra.getPathLength(target2);
		}
		return cost;
	}
}
