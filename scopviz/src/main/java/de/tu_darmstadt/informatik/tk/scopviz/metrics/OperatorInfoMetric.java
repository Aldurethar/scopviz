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

public class OperatorInfoMetric implements ScopvizGraphMetric{

	@Override
	public boolean isSetupRequired() {
		return false;
	}

	@Override
	public String getName() {
		return "Operator Info";
	}
	
	@Override
	public void setup() {
		// No Setup needed.		
	}
	
	@Override
	public LinkedList<Pair<String, String>> calculate(MyGraph g) {
		LinkedList<Pair<String, String>> result = new LinkedList<Pair<String,String>>();
		
		for (MyGraph subGraph: g.getAllSubGraphs()){
			if (subGraph.getAttribute("layer") == Layer.OPERATOR && !subGraph.isComposite()){
				String graphId = subGraph.getId();
				String info = "";
				Double priority = Double.valueOf(subGraph.getAttribute("priority"));
				boolean placed = fullyPlaced(subGraph, g);
				
				info = info.concat("Priority: " + priority.doubleValue());
				if (placed){
					info = info.concat(", fully placed.");
				} else {
					info = info.concat(", not fully placed.");
				}
				
				result.add(new Pair<String, String>(graphId, info));
			}
		}
		
		return result;
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
