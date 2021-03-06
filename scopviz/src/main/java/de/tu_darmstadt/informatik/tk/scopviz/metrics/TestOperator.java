package de.tu_darmstadt.informatik.tk.scopviz.metrics;

import java.util.Iterator;

import de.tu_darmstadt.informatik.tk.scopviz.graphs.GraphManager;
import de.tu_darmstadt.informatik.tk.scopviz.graphs.MyNode;
import de.tu_darmstadt.informatik.tk.scopviz.metrics.interfaces.ScopvizGraphOperator;

public class TestOperator implements ScopvizGraphOperator {

	@Override
	public void calculate(GraphManager g) {
		Iterator<MyNode> nodeIter = g.getGraph().getNodeIterator();
		while (nodeIter.hasNext()) {
			nodeIter.next().addAttribute("ui.style", "fill-color: blue;");
		}
	}

	@Override
	public String getName() {
		return "TestOperator";
	}

}
