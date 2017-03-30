package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import de.tu_darmstadt.informatik.tk.scopviz.debug.Debug;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MappingGraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyNode;
import de.tu_darmstadt.informatik.tk.scopviz.main.Main;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphOperator;

public class BasicMappingOperator implements ScopvizGraphOperator {

	@Override
	public void calculate(GraphManager g) {
		// check if you are using a Mapping Graph
		MappingGraphManager map;
		if (g instanceof MappingGraphManager) {
			map = (MappingGraphManager) g;
		} else {
			Debug.out("ERROR: can only invoke " + getName() + " on a Mapping Graph", 3);
			return;
		}

		// find the Nodes that have to be mapped and where they can be mapped to
		LinkedList<MyNode> operatorNodes = getOperatorNodes(map);
		LinkedList<MyNode> procEnNodes = getProcEnNodes(map);

		// Map the Nodes (beginning with the operatorNode with the highest
		// Processing requirement)
		operatorNodes.sort(operatorComparator);
		Iterator<MyNode> procEnIterator;
		Boolean successfull;
		for (MyNode n : operatorNodes) {
			procEnIterator = procEnNodes.iterator();
			successfull = false;
			while (procEnIterator.hasNext() && !successfull) {
				successfull = map.createEdge(procEnIterator.next().getId(), n.getId());
			}
			if (!successfull) {
				Debug.out("WARNING: BasicMappingOperator could not map all Nodes", 2);
			}

		}
	}

	@Override
	public String getName() {
		return "Basic Automapping";
	}

	protected LinkedList<MyNode> getProcEnNodes(GraphManager g) {
		LinkedList<MyNode> result = new LinkedList<MyNode>();
		Iterator<MyNode> nodeIter = g.getGraph().getNodeIterator();
		while (nodeIter.hasNext()) {
			MyNode n = nodeIter.next();
			if ("procEn".equals(n.getAttribute("typeofNode"))) {
				result.add(n);
			}
		}
		return result;
	}

	protected LinkedList<MyNode> getOperatorNodes(GraphManager g) {
		LinkedList<MyNode> result = new LinkedList<MyNode>();
		Iterator<MyNode> nodeIter = g.getGraph().getNodeIterator();
		while (nodeIter.hasNext()) {
			MyNode n = nodeIter.next();
			if ("operator".equals(n.getAttribute("typeofNode"))) {
				result.add(n);
			}
		}
		return result;
	}

	protected Comparator<MyNode> operatorComparator = new Comparator<MyNode>() {

		@Override
		public int compare(MyNode o1, MyNode o2) {
			Main m = Main.getInstance();

			// the cmparator uses a reverse ordering so that the resulting list
			// is sorted descending
			// this does: process-need(o1) - process-need(o2)
			Double result = m.convertAttributeTypes(o1.getAttribute("process-need"), new Double(0))
					- m.convertAttributeTypes(o2.getAttribute("process-need"), new Double(0));
			if (result == 0.0) {
				return 0;
			} else if (result < 0.0) {
				return 1;
			} else {
				return -1;
			}
		}
	};

}
