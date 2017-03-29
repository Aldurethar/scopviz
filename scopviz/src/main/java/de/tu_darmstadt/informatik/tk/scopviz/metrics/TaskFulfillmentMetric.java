package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.main.Layer;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.util.Pair;

public class TaskFulfillmentMetric implements ScopvizGraphMetric {

	/** The text to display in case of an error during computation. */
	private static final Pair<String, String> ERROR_MESSAGE = new Pair<String, String>("Error",
			"Operator Graph without priority found");

	/** Flag for when an operator graph without a priority is found */
	private boolean error = false;

	@Override
	public boolean isSetupRequired() {
		return false;
	}

	@Override
	public String getName() {
		return "Task Fulfillment";
	}

	@Override
	public void setup() {
		// No Setup required.
	}

	@Override
	public LinkedList<Pair<String, String>> calculate(MyGraph g) {
		LinkedList<Pair<String, String>> results = new LinkedList<Pair<String, String>>();

		// This corresponds to the Function F
		double placedSum = 0;
		// This corresponds to Fmax
		double prioritySum = 0;

		if (g.isComposite()) {
			LinkedList<MyGraph> graphs = g.getAllSubGraphs();
			for (MyGraph current : graphs) {
				if (current.getAttribute("layer") == Layer.OPERATOR && !current.isComposite()) {
					Double priority = Double.valueOf(current.getAttribute("priority"));
					if (priority == null) {
						error = true;
					} else {
						boolean placed = fullyPlaced(current, g);
						if (placed) {
							placedSum += priority;
						}
						prioritySum += priority;
					}
				}
			}
		}
		// This corresponds to F'
		double percentagePlaced = (placedSum / prioritySum) * 100;

		if (error) {
			error = false;
			results.add(ERROR_MESSAGE);
		}

		results.add(new Pair<String, String>("Task Placement", "" + placedSum));
		results.add(new Pair<String, String>("Placement Percentage", percentagePlaced + "%"));

		return results;
	}

	/**
	 * Checks whether an operator Graph is fully mapped onto the underlay.
	 * 
	 * @param operator
	 *            the operator Graph
	 * @param mapping
	 *            the mapping Graph
	 * @return true if all Nodes of the operator Graph have a valid mapping
	 */
	private boolean fullyPlaced(MyGraph operator, MyGraph mapping) {
		boolean result = true;
		LinkedList<Edge> mappingEdges = new LinkedList<Edge>(mapping.getEdgeSet().stream()
				.filter(e -> (((Boolean) e.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING)) == true))
				.collect(Collectors.toList()));
		// build list of the operator nodes within the mapping graph
		LinkedList<Node> operatorNodes = new LinkedList<Node>();
		for (Node n : mapping.getNodeSet()) {
			String originalGraph = n.getAttribute("originalGraph");
			if ((originalGraph != null && originalGraph.equals(operator.getId()))
					|| n.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING_PARENT_ID).equals(operator.getId())) {
				operatorNodes.add(n);
			}
		}
		// check if they have a mapping
		for (Node n : operatorNodes) {
			boolean isMapped = false;
			for (Edge e : mappingEdges) {
				if (e.getNode0().getId().equals(n.getId())) {
					isMapped = true;
				}
			}
			if (!isMapped) {
				result = false;
			}
		}

		return result;
	}

}
