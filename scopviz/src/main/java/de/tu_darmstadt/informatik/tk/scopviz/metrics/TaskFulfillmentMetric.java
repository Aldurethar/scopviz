package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyGraph;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphMetric;
import javafx.util.Pair;

//TODO: TaksFulfillmentMetric not yet implemented due to missing support for graph attributes.
public class TaskFulfillmentMetric implements ScopvizGraphMetric {

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
		LinkedList<Edge> mappingEdges = new LinkedList<Edge>(g.getEdgeSet().stream()
				.filter(e -> (((Boolean) e.getAttribute(MappingGraphManager.ATTRIBUTE_KEY_MAPPING)) == true))
				.collect(Collectors.toList()));
		return null;
	}
	
	
}
